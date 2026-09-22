package in.gov.dilrmp.models.reportDTO.mrr;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "mrr_report_view")
public class MrrViewReport {


    @Id
    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "lgd_code")
    private Integer lgdCode;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "total_district")
    private Integer totalDistrict;

    @Transient
    private String formattingTotalDistrict;


    @Column(name = "total_tehsils")
    private Integer totalTehsils;
    @Transient
    private String formattingTotalTehsils;

    @Column(name = "mrr_sanctioned")
    private Integer mrrSanctioned;

    @Transient
    private String formattingMrrSanctioned;

    @Column(name = "mrr_completed")
    private Integer mrrCompleted;

    @Transient
    private String formattingMrrCompleted;


    @Column(name = "mrr_sanctioned_percent")
    private Double mrrSanctionedPercent;

    @Column(name = "mrr_completed_out_of_sanctioned_percent")
    private Double mrrCompletedOutOfSanctionedPercent;

    @Column(name = "mrr_completed_out_of_total_tehsils_percent")
    private BigDecimal mrrCompletedOutOfTotalTehsilsPercent;

}