package in.gov.dilrmp.utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {
    private String baseDirectory = "C:\\Utilization Certificate"; // Specify the C drive path

}
