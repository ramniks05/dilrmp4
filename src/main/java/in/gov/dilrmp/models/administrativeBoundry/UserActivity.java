package in.gov.dilrmp.models.administrativeBoundry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String activity;
    @ManyToOne
    @JoinColumn(name = "userLoginHistoryID")
    private UserLoginHistory userLoginHistory;

    private LocalDateTime activeTime;
}
