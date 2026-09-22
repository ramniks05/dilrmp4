package in.gov.dilrmp.models.administrativeBoundry;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AdministrativeBoundry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message ="{name.notEmpty}")
    private String name;
    @Column(unique = true,name = "lgd_code")
    private Integer lgdCode;
    private String boundryType;
   

}
