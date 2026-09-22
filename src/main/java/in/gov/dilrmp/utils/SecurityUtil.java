package in.gov.dilrmp.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SecurityUtil {
     static String token;
     public static String getToken() {
        return token;
    }
    // Logger instance for logging exception details
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    public static void generateOneTimeToken() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);

        // Encode the byte array to a Base64 string
        String base64Token = Base64.getEncoder().encodeToString(randomBytes);

        // Remove non-alphanumeric characters
        base64Token = base64Token.replaceAll("[^a-zA-Z0-9]", "");

        // Trim the string to a desired length
        base64Token = base64Token.substring(0, 20); // Adjust the length as needed
        token=base64Token;
    }
    public static String hashWithSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Log the exception details
            logger.error("Error occurred while hashing with SHA-256", e);

            // Return a generic error message to the user
            return "An error occurred while processing the request. Please try again later.";

        }
    }
}
