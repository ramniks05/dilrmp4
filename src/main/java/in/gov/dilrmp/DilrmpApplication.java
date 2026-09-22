package in.gov.dilrmp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ServletComponentScan
@EnableScheduling  // Enable scheduling for your application
public class DilrmpApplication {
		public static void main(String[] args) {
				SpringApplication.run(DilrmpApplication.class, args);

	}

}
