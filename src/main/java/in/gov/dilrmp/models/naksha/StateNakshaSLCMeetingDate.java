package in.gov.dilrmp.models.naksha;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;


@Entity
@Table(name = "naksha_slcmeeting_date")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StateNakshaSLCMeetingDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "entry_id")
    private NakshaMISDataEntry nakshaMISDataEntry;


    @Column(name = "meeting_date")
    private LocalDate meetingDate;

}
