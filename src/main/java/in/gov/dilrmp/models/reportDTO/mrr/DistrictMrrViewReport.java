package in.gov.dilrmp.models.reportDTO.mrr;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "district_mrr_report_view")
public class DistrictMrrViewReport {


    @Id
    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "district_lgd_code")
    private Integer districtLgdCode;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "total_tehsils")
    private Long totalTehsils;

    @Column(name = "mrr_sanctioned")
    private Integer mrrSanctioned;

    @Column(name = "mrr_completed")
    private Integer mrrCompleted;

    @Column(name = "mrr_sanctioned_percent")
    private Double mrrSanctionedPercent;

    @Column(name = "mrr_completed_out_of_sanctioned_percent")
    private Double mrrCompletedOutOfSanctionedPercent;

    @Column(name = "mrr_completed_out_of_total_tehsils_percent")
    private Double mrrCompletedOutOfTotalTehsilsPercent;

}
