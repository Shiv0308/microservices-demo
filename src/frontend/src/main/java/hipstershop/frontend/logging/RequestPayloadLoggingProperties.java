package hipstershop.frontend.logging;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** Configuration for {@link RequestPayloadLoggingFilter}, bound from {@code replay.logging.*}. */
@Component
@ConfigurationProperties(prefix = "replay.logging")
public class RequestPayloadLoggingProperties {

    private boolean enabled = true;
    private int maxPayload = 10_000;
    private boolean includeResponse = false;
    private List<String> skipPaths = List.of(
            "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger.json", "/swagger/**",
            "/static/**", "/_healthz", "/robots.txt");
    private List<String> maskFields = List.of(
            "password", "passwd", "token", "authorization", "secret", "apikey", "api_key",
            "jwt", "cookie", "creditcard", "credit_card", "cvv", "ssn", "pin");

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxPayload() {
        return maxPayload;
    }

    public void setMaxPayload(int maxPayload) {
        this.maxPayload = maxPayload;
    }

    public boolean isIncludeResponse() {
        return includeResponse;
    }

    public void setIncludeResponse(boolean includeResponse) {
        this.includeResponse = includeResponse;
    }

    public List<String> getSkipPaths() {
        return skipPaths;
    }

    public void setSkipPaths(List<String> skipPaths) {
        this.skipPaths = skipPaths;
    }

    public List<String> getMaskFields() {
        return maskFields;
    }

    public void setMaskFields(List<String> maskFields) {
        this.maskFields = maskFields;
    }
}
