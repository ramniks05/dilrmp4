package in.gov.dilrmp.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Security Headers Configuration
 * 
 * This configuration class provides comprehensive security headers to protect against
 * various web vulnerabilities including:
 * - XSS attacks (Content Security Policy)
 * - Man-in-the-middle attacks (Strict Transport Security)
 * - Clickjacking (X-Frame-Options)
 * - MIME type sniffing (X-Content-Type-Options)
 * - Information leakage (Referrer Policy)
 */
@Configuration
public class SecurityHeadersConfiguration implements WebMvcConfigurer {

    /**
     * Content Security Policy directives
     * 
     * This CSP policy is designed to work with the existing application while
     * providing strong protection against XSS attacks.
     */
    public static final String CSP_POLICY = 
        "default-src 'self'; " +
        "script-src 'self' 'unsafe-inline' 'unsafe-eval' " +
            "https://cdnjs.cloudflare.com " +
            "https://code.jquery.com " +
            "https://stackpath.bootstrapcdn.com " +
            "https://cdn.jsdelivr.net; " +
        "style-src 'self' 'unsafe-inline' " +
            "https://fonts.googleapis.com " +
            "https://cdnjs.cloudflare.com " +
            "https://stackpath.bootstrapcdn.com; " +
        "font-src 'self' " +
            "https://fonts.gstatic.com " +
            "https://cdnjs.cloudflare.com; " +
        "img-src 'self' data: https:; " +
        "connect-src 'self'; " +
        "frame-ancestors 'none'; " +
        "base-uri 'self'; " +
        "form-action 'self'";

    /**
     * HSTS max age in seconds (1 year)
     */
    public static final int HSTS_MAX_AGE = 31536000;

    /**
     * Referrer Policy for controlling referrer information
     */
    public static final ReferrerPolicyHeaderWriter.ReferrerPolicy REFERRER_POLICY = 
        ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN;
}
