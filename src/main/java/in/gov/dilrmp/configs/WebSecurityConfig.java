package in.gov.dilrmp.configs;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSecurityConfig {

    @Bean
    public FilterRegistrationBean<HtmlInjectionFilter> htmlInjectionFilter() {
        FilterRegistrationBean<HtmlInjectionFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new HtmlInjectionFilter());
       // registrationBean.addUrlPatterns("/tehsil/save-tehsil"); // URL patterns to apply the filter
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

}