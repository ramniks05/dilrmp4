package in.gov.dilrmp.models.cna;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComponentRowInput {

    private Integer serialNo;
    private String sanctionIssued;
    private String sanctionCancelled;
    private String fundReleased;
    private String fundReleasedDate;
    private String fundBalance;
}
