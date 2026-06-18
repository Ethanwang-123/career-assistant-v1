package com.example.applicationtracker.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final String secret;
    private final long expirationMs;
    private final ObjectMapper objectMapper;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs,
            ObjectMapper objectMapper
    ) {
        this.secret = secret;
        this.expirationMs = expirationMs;
        this.objectMapper = objectMapper;
    }

    public String generateToken(String email) {
        try {
            Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", email);
            payload.put("iat", Instant.now().getEpochSecond());
            payload.put("exp", Instant.now().plusMillis(expirationMs).getEpochSecond());

            String headerPart = encode(objectMapper.writeValueAsBytes(header));
            String payloadPart = encode(objectMapper.writeValueAsBytes(payload));
            String unsignedToken = headerPart + "." + payloadPart;
            return unsignedToken + "." + sign(unsignedToken);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not generate JWT", exception);
        }
    }

    public String extractEmail(String token) {
        return readPayload(token).get("sub").toString();
    }

    public boolean isValid(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }
            String unsignedToken = parts[0] + "." + parts[1];
            if (!constantTimeEquals(sign(unsignedToken), parts[2])) {
                return false;
            }
            Number expiration = (Number) readPayload(token).get("exp");
            return expiration.longValue() > Instant.now().getEpochSecond();
        } catch (Exception exception) {
            return false;
        }
    }

    private Map<String, Object> readPayload(String token) {
        try {
            String[] parts = token.split("\\.");
            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            return objectMapper.readValue(payloadBytes, new TypeReference<>() {
            });
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid JWT", exception);
        }
    }

    private String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return encode(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    }

    private boolean constantTimeEquals(String first, String second) {
        if (first.length() != second.length()) {
            return false;
        }
        int result = 0;
        for (int index = 0; index < first.length(); index++) {
            result |= first.charAt(index) ^ second.charAt(index);
        }
        return result == 0;
    }
}
