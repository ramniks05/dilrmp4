package in.gov.dilrmp.models.naksha;

import lombok.Data;
@Data
public class CapacityBuildingReportDTO {
    private String state_name;

    private Integer grandTotalNigstMasterTrainers;
    private Integer grandTotalCoeMasterTrainers;
    private Integer grandTotalTotalMasterTrainers;
    private Integer grandTotalFieldTeamsSanctioned;
    private Integer grandTotalMembersTrained;
    private Integer grandTotalMembersToBeTrained;

    private String stateName;
    private String nameOfCoE;
    private Integer nigstMasterTrainers;

    private Double percentageTrained;


}

