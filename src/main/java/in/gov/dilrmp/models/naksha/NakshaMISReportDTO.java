package in.gov.dilrmp.models.naksha;

import lombok.Data;

@Data
public class NakshaMISReportDTO {

    private Long stateId;
    private String state_name;
    private String spmuRecruitmentCompleted;
    private Integer totalSPMUPositionsSanctioned;
    private Integer totalProfessionalsRecruited;
    private Integer teamsFormedForsanctioned;
    private Integer teamsFormedForFieldSurvey;
    private Integer roversSanctioned;
    private Integer roversProcuredForFieldSurvey;

    public NakshaMISReportDTO(Long stateId, String state_name, String spmuRecruitmentCompleted,
                              Integer totalSPMUPositionsSanctioned, Integer totalProfessionalsRecruited,
                              Integer teamsFormedForsanctioned, Integer teamsFormedForFieldSurvey,
                              Integer roversSanctioned, Integer roversProcuredForFieldSurvey) {
        this.stateId = stateId;
        this.state_name = state_name;
        this.spmuRecruitmentCompleted = spmuRecruitmentCompleted;
        this.totalSPMUPositionsSanctioned = totalSPMUPositionsSanctioned != null ? totalSPMUPositionsSanctioned : 0;
        this.totalProfessionalsRecruited = totalProfessionalsRecruited != null ? totalProfessionalsRecruited : 0;
        this.teamsFormedForsanctioned = teamsFormedForsanctioned != null ? teamsFormedForsanctioned : 0;
        this.teamsFormedForFieldSurvey = teamsFormedForFieldSurvey != null ? teamsFormedForFieldSurvey : 0;
        this.roversSanctioned = roversSanctioned != null ? roversSanctioned : 0;
        this.roversProcuredForFieldSurvey = roversProcuredForFieldSurvey != null ? roversProcuredForFieldSurvey : 0;
    }



}
