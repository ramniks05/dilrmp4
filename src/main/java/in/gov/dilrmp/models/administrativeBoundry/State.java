package in.gov.dilrmp.models.administrativeBoundry;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import in.gov.dilrmp.models.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class State extends AdministrativeBoundry implements Serializable {
    public static final String ADMINSTRATATIVE_BOUNDRY_TYPE_STATE="1";
    public static final String STATE="STATE";
    public static final String UT_STATE="UT";
    @NotBlank(message = "State type must not be blank")
    private String stateType;
    @OneToMany(mappedBy = "state")
    private List<District> districtList;
    @OneToOne(cascade = {CascadeType.PERSIST,CascadeType.REMOVE}, fetch = FetchType.LAZY)
    private User muser;
   
   

}
