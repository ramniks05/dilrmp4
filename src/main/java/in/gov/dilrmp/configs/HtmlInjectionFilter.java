package in.gov.dilrmp.configs;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

public class HtmlInjectionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code here
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse res = (HttpServletResponse) servletResponse;
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        res.setHeader("Access-Control-Max-Age", "3600");
        // Create a wrapper around the request to modify parameter values
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request) {
            @Override
            public String[] getParameterValues(String parameter) {
                // Sanitize each parameter value before returning
                String[] originalValues = super.getParameterValues(parameter);
                if (originalValues == null) {
                    return null;
                }
                String[] sanitizedValues = new String[originalValues.length];
                for (int i = 0; i < originalValues.length; i++) {
                    sanitizedValues[i] = sanitizeHtml(originalValues[i]);
                }
                return sanitizedValues;
            }

            @Override
            public Enumeration<String> getParameterNames() {
                // Return sanitized parameter names
                return Collections.enumeration(Collections.list(super.getParameterNames()));
            }

            @Override
            public String getParameter(String name) {
                // Return sanitized parameter value
                String value = super.getParameter(name);
                return value != null ? sanitizeHtml(value) : null;
            }
        };

        // Continue with the filter chain using the modified request
        filterChain.doFilter(requestWrapper, servletResponse);
    }

    @Override
    public void destroy() {
        // Cleanup code here
    }

    private String sanitizeHtml(String input) {
        // Implement your sanitization logic here
        // Example: Remove HTML tags
        return input.replaceAll("<[^>]*>", "");
    }


}