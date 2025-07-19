package com.busoft.cognitoService.hash;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
public class SecretHash {

    public static String calculateSecretHash(String username, String clientId, String clientSecret) {
        try {
            final String HMAC_ALGORITHM = "HmacSHA256";
            String data = username + clientId;

            SecretKeySpec signingKey = new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Unable to calculate secret hash", e);
        }
    }

}
