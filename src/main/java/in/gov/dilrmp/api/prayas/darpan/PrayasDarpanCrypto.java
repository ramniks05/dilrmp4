package in.gov.dilrmp.api.prayas.darpan;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

/**
 * Legacy DARPAN payload: GZIP (4-byte LE length prefix) then AES/CBC/PKCS5 with key file.
 */
@Component
public class PrayasDarpanCrypto {

    private final PrayasDarpanProperties properties;

    public PrayasDarpanCrypto(PrayasDarpanProperties properties) {
        this.properties = properties;
    }

    public String compress(String str) throws IOException {
        byte[] lengthPrefix = ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(str.length())
                .array();
        ByteArrayOutputStream os = new ByteArrayOutputStream(str.length());
        try (GZIPOutputStream gos = new GZIPOutputStream(os)) {
            gos.write(str.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
        byte[] gzipBytes = os.toByteArray();
        byte[] compressed = new byte[4 + gzipBytes.length];
        System.arraycopy(lengthPrefix, 0, compressed, 0, 4);
        System.arraycopy(gzipBytes, 0, compressed, 4, gzipBytes.length);
        return Base64.getEncoder().encodeToString(compressed);
    }

    public String encrypt(String str) throws Exception {
        byte[] key = readKeyBytes();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
        byte[] keyBytes = new byte[16];
        int len = Math.min(key.length, keyBytes.length);
        System.arraycopy(key, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] results = cipher.doFinal(str.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(results);
    }

    private byte[] readKeyBytes() throws IOException {
        ClassPathResource resource = new ClassPathResource(properties.getKeyClasspath());
        if (!resource.exists()) {
            throw new IOException("DARPAN key not found on classpath: " + properties.getKeyClasspath());
        }
        try (InputStream in = resource.getInputStream()) {
            return in.readAllBytes();
        }
    }
}
