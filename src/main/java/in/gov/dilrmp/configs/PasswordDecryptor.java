package in.gov.dilrmp.configs;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class PasswordDecryptor {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    public static String decrypt(String encryptedPasswordBase64, String encryptionKeyBase64, String ivBase64) throws Exception {
        byte[] encryptedPassword = Base64.getDecoder().decode(encryptedPasswordBase64);
        byte[] keyBytes = Base64.getDecoder().decode(encryptionKeyBase64);
        byte[] ivBytes = Base64.getDecoder().decode(ivBase64);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivBytes));
        byte[] decryptedBytes = cipher.doFinal(encryptedPassword);
        return new String(decryptedBytes);

    }
}