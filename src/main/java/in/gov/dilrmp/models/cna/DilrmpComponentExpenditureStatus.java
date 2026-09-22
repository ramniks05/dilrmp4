package in.gov.dilrmp.models.cna;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dilrmp_component_expenditure_status",
        uniqueConstraints = @UniqueConstraint(columnNames = {"dilrmp_phase", "state_id", "financial_year", "serial_no"}))
@Getter
@Setter
@NoArgsConstructor
public class DilrmpComponentExpenditureStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dilrmp_phase", nullable = false)
    private String dilrmpPhase;

    @Column(name = "state_id", nullable = false)
    private Long stateId;

    @Column(name = "state_name", nullable = false)
    private String stateName;

    @Column(name = "financial_year", nullable = false)
    private String financialYear;

    @Column(name = "serial_no", nullable = false)
    private Integer serialNo;

    @Column(name = "component_name", nullable = false, length = 500)
    private String componentName;

    @Column(name = "sanction_issued")
    private BigDecimal sanctionIssued;

    @Column(name = "sanction_cancelled")
    private BigDecimal sanctionCancelled;

    @Column(name = "fund_released")
    private BigDecimal fundReleased;

    @Column(name = "fund_released_date")
    private LocalDate fundReleasedDate;

    @Column(name = "fund_balance")
    private BigDecimal fundBalance;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;
}
