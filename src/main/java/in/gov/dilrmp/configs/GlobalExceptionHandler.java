package in.gov.dilrmp.configs;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;


@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(RequestRejectedException.class)
    public ResponseEntity<String> handleRequestRejectedException(RequestRejectedException ex) {
        // Customize your error message and response here
        String message = "NOT_ALLOWED";
        return new ResponseEntity<>(message, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleError(HttpServletRequest request, Exception e, Model model) {
        ModelAndView modelAndView = new ModelAndView("HttpStatusError");
        modelAndView.addObject("message", "Something went wrong, please try logging in again.");
        model.addAttribute("message", "Something went wrong, please try logging in again.");

        return new ModelAndView("pages/error/error");
    }
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleError404(HttpServletRequest request, Exception e)   {
         return new ModelAndView("pages/error/error");
    }

}


