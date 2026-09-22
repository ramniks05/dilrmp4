package in.gov.dilrmp.configs;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Map the CORS configuration to all endpoints
                .allowedOrigins("*") // Allow requests from any origin, adjust as needed
                //.allowedOrigins("http://localhost:8082", "https://dilrmp.gov.in") // Specify allowed origins
                .allowedMethods("*") // Specify allowed HTTP methods
                .allowedHeaders("*"); // Specify allowed headers
                // .allowedHeaders("*"); // Allow all headers
    }

}
