package in.gov.dilrmp.models.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames ="roleName"))
@Getter @Setter @AllArgsConstructor @NoArgsConstructor 
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roleName;
    private String roleDescription;
    public static final String USER_ROLE_DOLR ="ROLE_DOLR";
    public static final String USER_ROLE_STATE ="ROLE_STATE";
    public static final String USER_ROLE_DISTRICT = "ROLE_DISTRICT";
    public static final String USER_ROLE_IGR = "ROLE_IGR";

    public static final String USER_ROLE_ADMIN = "ROLE_ADMIN";
    public static final String USER_ROLE_ULB = "ROLE_ULB";
    public static final String USER_ROLE_STATEULB = "ROLE_STATEULB";
    public static final String USER_ROLE_GD = "ROLE_GD";
    public static final String USER_ROLE_SGO = "ROLE_SGO";

    public static final String USER_ROLE_ADC = "ROLE_ADC";


    
    


    
}
