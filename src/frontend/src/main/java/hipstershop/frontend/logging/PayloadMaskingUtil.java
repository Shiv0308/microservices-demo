package hipstershop.frontend.logging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Masks sensitive field values in request/response payloads and header values before logging. */
final class PayloadMaskingUtil {

    static final String MASK = "***MASKED***";

    private static final Pattern JSON_FIELD = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern FORM_FIELD = Pattern.compile("([^&=]+)=([^&]*)");

    private PayloadMaskingUtil() {
    }

    static boolean isSensitive(String fieldName, List<String> maskFields) {
        String lower = fieldName.toLowerCase();
        for (String masked : maskFields) {
            if (lower.contains(masked.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    static String maskHeaderValue(String name, String value, List<String> maskFields) {
        return isSensitive(name, maskFields) ? MASK : value;
    }

    /** Masks sensitive values inside a JSON or form-urlencoded payload string. */
    static String maskBody(String body, String contentType, List<String> maskFields, ObjectMapper mapper) {
        if (body == null || body.isEmpty()) {
            return body;
        }
        if (contentType != null && contentType.contains("json")) {
            try {
                JsonNode root = mapper.readTree(body);
                maskJsonNode(root, maskFields);
                return mapper.writeValueAsString(root);
            } catch (Exception e) {
                // Not valid/parseable JSON; fall through to regex-based masking.
            }
        }
        if (contentType != null && contentType.contains("form-urlencoded")) {
            return maskWithPattern(body, FORM_FIELD, maskFields);
        }
        return maskWithPattern(body, JSON_FIELD, maskFields);
    }

    private static void maskJsonNode(JsonNode node, List<String> maskFields) {
        if (node instanceof ObjectNode obj) {
            obj.fieldNames().forEachRemaining(name -> {
                if (isSensitive(name, maskFields)) {
                    obj.put(name, MASK);
                } else {
                    maskJsonNode(obj.get(name), maskFields);
                }
            });
        } else if (node instanceof ArrayNode array) {
            array.forEach(child -> maskJsonNode(child, maskFields));
        }
    }

    private static String maskWithPattern(String body, Pattern pattern, List<String> maskFields) {
        Matcher matcher = pattern.matcher(body);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = isSensitive(key, maskFields)
                    ? matcher.group(0).replace(matcher.group(2), MASK)
                    : matcher.group(0);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
