package in.gov.dilrmp.models.unspentBalance;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "state_unspent_balance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"state_id", "as_on_date", "component_code"}))
@Getter
@Setter
@NoArgsConstructor
public class UnspentBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "state_id", nullable = false)
    private Long stateId;

    @Column(name = "state_name", nullable = false)
    private String stateName;

    @Column(name = "as_on_date", nullable = false)
    private LocalDate asOnDate;

    @Column(name = "component_code", nullable = false, length = 20)
    private String componentCode;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "serial_no", length = 10)
    private String serialNo;

    @Column(name = "component_name", nullable = false, length = 500)
    private String componentName;

    @Column(name = "unspent_balance_crore", precision = 15, scale = 2)
    private BigDecimal unspentBalance;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;
}
