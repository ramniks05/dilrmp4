package in.gov.dilrmp.configs;

import in.gov.dilrmp.utils.SecurityUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CustomFilter extends OncePerRequestFilter {

  
    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request, @SuppressWarnings("null") HttpServletResponse response, @SuppressWarnings("null") FilterChain filterChain) throws ServletException, IOException {
        String path=request.getContextPath();
        if ((path+"/login").equals(request.getRequestURI())
                && "POST".equals(request.getMethod())) {
            String captcha = request.getParameter("captcha");
            if (!SecurityUtils.validateCaptcha(captcha, request)) {
                // Redirect to login page with error parameter
                response.sendRedirect(path+"/loginPage?errorCaptcha=captcha");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }


}
