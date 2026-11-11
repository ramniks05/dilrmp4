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

    /** Bank Balance Last Shared On from the Excel row (may differ by CNA). */
    @Column(name = "report_date")
    private LocalDate reportDate;

    /** DW Report data last refreshed on — one date for the whole Excel file; used for filter and duplicate check. */
    @Column(name = "as_on_date")
    private LocalDate asOnDate;

    @Column(name = "uploaded_on")
    private LocalDateTime uploadedOn;

    @Transient
    private String stateName;

    @Transient
    private Integer lgdCode;
}
