package in.gov.dilrmp.models.naksha;

import lombok.Data;

@Data
public class NakshaMisStateDTO {
    private Long stateId;
    private String stateName;

    private Long districtId;
    private String districtName;

    private Long id;
    private String ulbName;

    public NakshaMisStateDTO(Long stateId, String stateName) {
        this.stateId = stateId;
        this.stateName = stateName;
    }

    public NakshaMisStateDTO(String districtName,Long districtId) {
        this.districtId = districtId;
        this.districtName = districtName;
    }

    public NakshaMisStateDTO(Long id, String ulbName,String districtName) {
        this.id = id;
        this.ulbName = ulbName;
    }
}

