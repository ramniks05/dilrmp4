package in.gov.dilrmp.models.administrativeBoundry;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityBean {

    private Long id;
    private String activity;

    private String name;

    private String activeTime;
}
