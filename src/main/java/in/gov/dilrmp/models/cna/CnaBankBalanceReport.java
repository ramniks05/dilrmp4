package in.gov.dilrmp.models.cna;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cna_bank_balance_report")
@Getter
@Setter
@NoArgsConstructor
public class CnaBankBalanceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cna_code", nullable = false)
    private String cnaCode;

    @Column(name = "cna_name", nullable = false)
    private String cnaName;

    @Column(name = "bank_balance", nullable = false)
    private BigDecimal bankBalance;

    @Column(name = "report_date")
    private LocalDate reportDate;

    @Column(name = "uploaded_on")
    private LocalDateTime uploadedOn;
}
