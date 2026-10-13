package in.gov.dilrmp.controllers.site;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/site")
public class SiteInfoController {

    @GetMapping("/website-policies")
    public String websitePolicies() {
        return "pages/site/website-policies";
    }

    @GetMapping("/help")
    public String help() {
        return "pages/site/help";
    }

    @GetMapping("/sitemap")
    public String sitemap() {
        return "pages/site/sitemap";
    }

    @GetMapping("/feedback")
    public String feedback() {
        return "pages/site/feedback";
    }
}
