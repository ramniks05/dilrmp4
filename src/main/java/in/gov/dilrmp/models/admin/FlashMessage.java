package in.gov.dilrmp.models.admin;
import in.gov.dilrmp.models.administrativeBoundry.State;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Entity
public class FlashMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "state_id")
    private State state;

    private String message;

    private String messageType;

    private LocalDate localDate;

    private LocalDate startDate;
    private LocalDate endDate;

    @Transient
    private String stringStartDate;
    @Transient
    private String stringEndDate;


    public void setLocalDate() {
        if (this.localDate == null) {
            this.localDate = LocalDate.now();
        }
    }

}
