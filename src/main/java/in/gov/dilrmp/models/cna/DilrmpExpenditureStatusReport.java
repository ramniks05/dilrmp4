package in.gov.dilrmp.models.cna;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dilrmp_expenditure_status_report")
@Getter
@Setter
@NoArgsConstructor
public class DilrmpExpenditureStatusReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "serial_no")
    private Integer serialNo;

    @Column(name = "state_name", nullable = false)
    private String stateName;

    @Column(name = "release_by_center")
    private BigDecimal releaseByCenter;

    @Column(name = "cna_opening_balance")
    private BigDecimal cnaOpeningBalance;

    @Column(name = "expenditure_april")
    private BigDecimal expenditureApril;

    @Column(name = "expenditure_may")
    private BigDecimal expenditureMay;

    @Column(name = "expenditure_june")
    private BigDecimal expenditureJune;

    @Column(name = "total_expenditure")
    private BigDecimal totalExpenditure;

    @Column(name = "cna_balance")
    private BigDecimal cnaBalance;

    @Column(name = "financial_year")
    private String financialYear;

    @Column(name = "report_as_on_date")
    private LocalDate reportAsOnDate;

    @Column(name = "expenditure_label_1")
    private String expenditureLabel1;

    @Column(name = "expenditure_label_2")
    private String expenditureLabel2;

    @Column(name = "expenditure_label_3")
    private String expenditureLabel3;

    @Column(name = "opening_balance_header")
    private String openingBalanceHeader;

    @Column(name = "total_expenditure_header")
    private String totalExpenditureHeader;

    @Column(name = "cna_balance_header")
    private String cnaBalanceHeader;

    @Column(name = "uploaded_on")
    private LocalDateTime uploadedOn;
}
