package in.gov.dilrmp.configs;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HtmlInjectionInterceptor implements HandlerInterceptor {
    @SuppressWarnings("null")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Set Content Security Policy (CSP) headers to restrict HTML Injection
        response.setHeader("Content-Security-Policy", "script-src 'self'");
        return true;
    }
}
