package in.gov.dilrmp.utils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.security.SecureRandom;
public class PasswordUtils {

    private static final int SALT_LENGTH = 16; // Length of the salt
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return bytesToHex(salt);
    }

    public static String hashPassword(String password, String salt) {
        return passwordEncoder.encode(password + salt);
    }

    public static boolean verifyPassword(String rawPassword, String encodedPassword, String salt) {
        String hashedPassword = hashPassword(rawPassword, salt);
        return passwordEncoder.matches(encodedPassword, hashedPassword);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
