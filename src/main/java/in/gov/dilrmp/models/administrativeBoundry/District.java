package in.gov.dilrmp.models.administrativeBoundry;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import in.gov.dilrmp.models.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "district", uniqueConstraints = @UniqueConstraint(name = "uk_district_lgd_code", columnNames = "lgd_code"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class District extends AdministrativeBoundry implements Serializable {
   
    public static final String ADMINSTRATATIVE_BOUNDRY_TYPE_DISTRICT = "2";

    @ManyToOne()
    private State state;
    @ManyToOne()
    private Adc adc;
    @Transient
    private Long toStateID;
    @Transient
    private Long fromStateID;
    @Transient
    private List<Long> selectedDistrictID;
    @OneToOne(cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    private User muser;
    //private Long  muser_adc_id;
}
