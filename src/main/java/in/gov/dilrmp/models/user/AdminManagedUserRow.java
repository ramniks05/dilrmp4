package in.gov.dilrmp.models.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminManagedUserRow {
    private String username;
    private String stateName;
    private String districtName;
    private String lgdCode;
    private String role;
    private String accountStatus;
}
