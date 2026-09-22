package in.gov.dilrmp.models.administrativeBoundry;

import in.gov.dilrmp.models.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Data


public class Adc extends AdministrativeBoundry implements Serializable {
    public static final String ADMINSTRATATIVE_BOUNDRY_TYPE_DISTRICT = "2";

    @ManyToOne()
    private State state;
    @Transient
    private Long toStateID;
    @Transient
    private Long fromStateID;
    @Transient
    private List<Long> selectedDistrictID;
    @OneToMany(mappedBy = "adc")
    private List<District> districtList;
    @OneToOne(cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    private User muser;

}
