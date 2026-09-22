package in.gov.dilrmp.controllers.captcha;



import in.gov.dilrmp.services.captchaService.CaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.SecureRandom;

@Controller
@RequestMapping("/dilrmp")
public class CaptchaController {

    private static final Logger logger = LoggerFactory.getLogger(CaptchaController.class);
    public CaptchaController(CaptchaService captchaService) {
    }

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("Generating CAPTCHA");

        int captchaLength = 6;
        String captcha = generateCaptcha(captchaLength);

        request.getSession().setAttribute("captchaText", captcha); // Store the CAPTCHA in session

        int width = 200;
        int height = 50;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = null;

        try {
            g = image.getGraphics();

            // Set background color
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            // Draw CAPTCHA text
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            g.drawString(captcha, 20, 35);

            // Add noise to the image
            SecureRandom secureRandom = new SecureRandom();
            for (int i = 0; i < width * height / 20; i++) {
                int x = secureRandom.nextInt(width);
                int y = secureRandom.nextInt(height);
                image.setRGB(x, y, secureRandom.nextInt(255));
            }

            // Add random lines for more noise
            for (int i = 0; i < 5; i++) {
                int startX = secureRandom.nextInt(width);
                int startY = secureRandom.nextInt(height);
                int endX = secureRandom.nextInt(width);
                int endY = secureRandom.nextInt(height);
                g.setColor(new Color(secureRandom.nextInt(255), secureRandom.nextInt(255), secureRandom.nextInt(255)));
                g.drawLine(startX, startY, endX, endY);
            }

            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            response.setContentType("image/png");
            ImageIO.write(image, "png", response.getOutputStream());
            logger.info("CAPTCHA image generated and sent to client");
        } catch (Exception e) {
            logger.error("Error generating CAPTCHA", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating CAPTCHA");
        } finally {
            if (g != null) {
                g.dispose(); // Release Graphics resources
            }
        }
    }


    private String generateCaptcha(int length) {
        String upperCaseCharacter = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseCharacter = "abcdefghijklmnopqrstuvwxyz";
        String numericCharacter = "0123456789";

        String uniqueCode = generateUniqueCode(upperCaseCharacter, lowerCaseCharacter, numericCharacter);
        logger.debug("Generated CAPTCHA: {}", uniqueCode);
        return uniqueCode;
    }

    @GetMapping("/verifyCaptcha")
    public ResponseEntity<String> verifyCaptcha(@RequestParam String enteredCaptcha, HttpServletRequest request) {
        String storedCaptcha = (String) request.getSession().getAttribute("captchaText");

        logger.info("Verifying CAPTCHA");

        if (storedCaptcha != null && storedCaptcha.trim().equalsIgnoreCase(enteredCaptcha.trim())) {
            logger.info("CAPTCHA verification successful");
            return ResponseEntity.ok("Captcha verification successful");
        } else {
            logger.warn("CAPTCHA verification failed");
            return ResponseEntity.badRequest().body("Captcha verification failed");
        }
    }


    public static String generateUniqueCode(String upperCaseChars, String lowerCaseChars, String numericChars) {
        SecureRandom secureRandom = null;
        StringBuilder codeBuilder = new StringBuilder();

        try {
            secureRandom = new SecureRandom();

            // Add 2 uppercase characters
            for (int i = 0; i < 2; i++) {
                int randomIndex = secureRandom.nextInt(upperCaseChars.length());
                codeBuilder.append(upperCaseChars.charAt(randomIndex));
            }

            // Add 2 lowercase characters
            for (int i = 0; i < 2; i++) {
                int randomIndex = secureRandom.nextInt(lowerCaseChars.length());
                codeBuilder.append(lowerCaseChars.charAt(randomIndex));
            }

            // Add 2 numeric characters
            for (int i = 0; i < 2; i++) {
                int randomIndex = secureRandom.nextInt(numericChars.length());
                codeBuilder.append(numericChars.charAt(randomIndex));
            }

            // Shuffle the characters to make the code unique
            for (int i = codeBuilder.length() - 1; i > 0; i--) {
                int index = secureRandom.nextInt(i + 1);
                char temp = codeBuilder.charAt(index);
                codeBuilder.setCharAt(index, codeBuilder.charAt(i));
                codeBuilder.setCharAt(i, temp);
            }

        } catch (Exception e) {
            logger.error("Error generating unique code", e);
            return null; // You can choose a fallback value or an error message here
        }

        String uniqueCode = codeBuilder.toString();
        logger.debug("Generated unique code: {}", uniqueCode);
        return uniqueCode;
    }

}