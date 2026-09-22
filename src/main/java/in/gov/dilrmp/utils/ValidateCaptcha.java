package in.gov.dilrmp.utils;

import jakarta.servlet.http.HttpServletRequest;

public class ValidateCaptcha {

    public static boolean validateCaptcha(String captcha, HttpServletRequest request) {

        String storedCaptchaText = (String) request.getSession().getAttribute("captchaText");// Assuming this method exists
        request.getSession().removeAttribute("captchaText");
        return storedCaptchaText != null && captcha != null && storedCaptchaText.equals(captcha);
    }


}
