package in.gov.dilrmp.services.captchaService;


import in.gov.dilrmp.models.captcha.Captcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class CaptchaServiceImpl implements CaptchaService {
    private static final Logger logger = LoggerFactory.getLogger(CaptchaServiceImpl.class);
    private static final int CAPTCHA_LENGTH = 6;

    @Override
    public Captcha generateCaptcha() {
        logger.info("Generating CAPTCHA with length: {}", CAPTCHA_LENGTH);
        String captchaText = generateRandomCaptchaText(CAPTCHA_LENGTH);
        logger.info("Generated CAPTCHA text: {}", captchaText);
        return new Captcha(captchaText);
    }

    private String generateRandomCaptchaText(int length) {
        logger.debug("Generating random CAPTCHA text of length: {}", length);
        SecureRandom secureRandom = new SecureRandom();

        StringBuilder captchaText = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < length; i++) {
            captchaText.append(characters.charAt(secureRandom.nextInt(characters.length())));
        }

        // Logging each generated character for debugging
        logger.debug("Generated CAPTCHA characters: {}", captchaText.toString());

        return captchaText.toString();
    }
}