package in.gov.dilrmp.controllers.error;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

        if (statusCode != null) {
            switch (statusCode) {
                case 403:
                    return "pages/error/403";
                case 404:
                    return "pages/error/404";
                case 500:
                    return "pages/error/500";
                case 400:
                    return "pages/error/400";
                default:
                    return "pages/error/error";
            }
        }

        return "pages/error/error";
    }


}
