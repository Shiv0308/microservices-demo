package hipstershop.frontend.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import hipstershop.frontend.session.SessionContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * Captures the full details of every incoming REST request (metadata + payload) into the
 * dedicated {@code hipstershop.frontend.payload} logger, which is routed by
 * {@code logback-spring.xml} to {@code logs/request_payload.log} only.
 *
 * <p>Runs after {@link hipstershop.frontend.tracing.HttpTracingFilter},
 * {@link hipstershop.frontend.session.SessionIdFilter} and
 * {@link hipstershop.frontend.logging.RequestLoggingFilter}, so it can reuse the request id,
 * session id and (if present) the {@link ContentCachingResponseWrapper} they already establish
 * instead of wrapping the response a second time.
 */
@Component
@Order(4)
public class RequestPayloadLoggingFilter extends OncePerRequestFilter {

    private static final Logger payloadLog = LoggerFactory.getLogger("hipstershop.frontend.payload");
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final RequestPayloadLoggingProperties props;
    private final ObjectMapper objectMapper;

    public RequestPayloadLoggingFilter(RequestPayloadLoggingProperties props, ObjectMapper objectMapper) {
        this.props = props;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!props.isEnabled()) {
            return true;
        }
        String path = request.getRequestURI();
        for (String pattern : props.getSkipPaths()) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestId = SessionContext.requestId(request);
        if (requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        response.setHeader("X-Request-ID", requestId);

        String contentType = request.getContentType();
        boolean cachePayload = isCacheableContentType(contentType);
        HttpServletRequest requestToUse = cachePayload
                ? new ContentCachingRequestWrapper(request, props.getMaxPayload())
                : request;

        ContentCachingResponseWrapper responseWrapper = null;
        HttpServletResponse responseToUse = response;
        boolean ownsResponseWrapper = false;
        if (props.isIncludeResponse()) {
            if (response instanceof ContentCachingResponseWrapper existing) {
                responseWrapper = existing;
            } else {
                responseWrapper = new ContentCachingResponseWrapper(response);
                responseToUse = responseWrapper;
                ownsResponseWrapper = true;
            }
        }

        long start = System.currentTimeMillis();
        Throwable failure = null;
        try {
            chain.doFilter(requestToUse, responseToUse);
        } catch (RuntimeException | ServletException | IOException ex) {
            failure = ex;
            throw ex;
        } finally {
            long durationMs = System.currentTimeMillis() - start;
            try {
                logRequest(requestToUse, response, responseWrapper, requestId, contentType, cachePayload,
                        durationMs, failure);
            } catch (Exception loggingFailure) {
                payloadLog.warn("Failed to write request payload log entry", loggingFailure);
            }
            if (ownsResponseWrapper) {
                responseWrapper.copyBodyToResponse();
            }
        }
    }

    private void logRequest(HttpServletRequest request, HttpServletResponse rawResponse,
            ContentCachingResponseWrapper responseWrapper, String requestId, String contentType,
            boolean cachePayload, long durationMs, Throwable failure) throws IOException {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("timestamp", Instant.now().toString());
        entry.put("requestId", requestId);
        entry.put("thread", Thread.currentThread().getName());
        entry.put("remoteIp", request.getRemoteAddr());
        entry.put("remoteHost", request.getRemoteHost());
        entry.put("sessionId", SessionContext.sessionId(request));
        entry.put("authenticatedUser", request.getRemoteUser());
        entry.put("principal", request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null);
        entry.put("method", request.getMethod());
        entry.put("uri", request.getRequestURI());
        entry.put("queryString", request.getQueryString());
        entry.put("status", rawResponse.getStatus());
        entry.put("durationMs", durationMs);
        entry.put("contentType", contentType);
        entry.put("characterEncoding", request.getCharacterEncoding());
        entry.put("contentLength", request.getContentLengthLong());
        entry.put("userAgent", request.getHeader("User-Agent"));
        entry.put("referer", request.getHeader("Referer"));
        entry.put("headers", maskedHeaders(request));
        entry.put("cookies", maskedCookies(request));

        putPayload(entry, request, contentType, cachePayload);

        if (props.isIncludeResponse() && responseWrapper != null) {
            entry.put("responseBody", maskedResponseBody(responseWrapper));
        }
        if (failure != null) {
            Map<String, Object> exceptionInfo = new LinkedHashMap<>();
            exceptionInfo.put("type", failure.getClass().getName());
            exceptionInfo.put("message", failure.getMessage());
            entry.put("exception", exceptionInfo);
        }

        payloadLog.info(objectMapper.writeValueAsString(entry));
    }

    private void putPayload(Map<String, Object> entry, HttpServletRequest request, String contentType,
            boolean cachePayload) {
        if (!cachePayload) {
            entry.put("payload", contentType == null
                    ? "[skipped: no body]"
                    : "[skipped: content-type not eligible for logging]");
            entry.put("payloadTruncated", false);
            entry.put("originalLength", request.getContentLengthLong());
            return;
        }
        ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
        byte[] cached = wrapper.getContentAsByteArray();
        String charset = request.getCharacterEncoding() != null ? request.getCharacterEncoding()
                : StandardCharsets.UTF_8.name();
        String body;
        try {
            body = new String(cached, charset);
        } catch (Exception e) {
            body = new String(cached, StandardCharsets.UTF_8);
        }
        long originalLength = request.getContentLengthLong() >= 0 ? request.getContentLengthLong() : cached.length;
        // ContentCachingRequestWrapper only caches bytes actually read by downstream code, so a body that
        // was never consumed (e.g. request rejected before controller binding, such as a 405) looks
        // identical to a short read. Only flag truncation when we actually hit our own cache limit.
        boolean truncated = cached.length >= props.getMaxPayload() && originalLength > cached.length;
        entry.put("payload", PayloadMaskingUtil.maskBody(body, contentType, props.getMaskFields(), objectMapper));
        entry.put("payloadTruncated", truncated);
        entry.put("originalLength", originalLength);
    }

    private String maskedResponseBody(ContentCachingResponseWrapper responseWrapper) {
        byte[] content = responseWrapper.getContentAsByteArray();
        if (content.length == 0) {
            return "";
        }
        int limit = Math.min(content.length, props.getMaxPayload());
        String body = new String(content, 0, limit, StandardCharsets.UTF_8);
        return PayloadMaskingUtil.maskBody(body, responseWrapper.getContentType(), props.getMaskFields(),
                objectMapper);
    }

    private Map<String, String> maskedHeaders(HttpServletRequest request) {
        Map<String, String> headers = new LinkedHashMap<>();
        Collections.list(request.getHeaderNames()).forEach(name ->
                headers.put(name, PayloadMaskingUtil.maskHeaderValue(name, request.getHeader(name),
                        props.getMaskFields())));
        return headers;
    }

    private Map<String, String> maskedCookies(HttpServletRequest request) {
        Map<String, String> cookies = new LinkedHashMap<>();
        Cookie[] requestCookies = request.getCookies();
        if (requestCookies != null) {
            for (Cookie cookie : requestCookies) {
                cookies.put(cookie.getName(),
                        PayloadMaskingUtil.maskHeaderValue(cookie.getName(), cookie.getValue(),
                                props.getMaskFields()));
            }
        }
        return cookies;
    }

    private boolean isCacheableContentType(String contentType) {
        if (contentType == null || contentType.isEmpty()) {
            return false;
        }
        String lower = contentType.toLowerCase();
        if (lower.startsWith("multipart/") || lower.contains("octet-stream") || lower.startsWith("image/")
                || lower.startsWith("video/") || lower.startsWith("audio/") || lower.contains("pdf")
                || lower.contains("zip")) {
            return false;
        }
        return lower.contains("json") || lower.contains("xml") || lower.startsWith("text/")
                || lower.contains("form-urlencoded");
    }
}
