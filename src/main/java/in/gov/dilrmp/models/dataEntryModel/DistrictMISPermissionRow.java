package in.gov.dilrmp.models.dataEntryModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictMISPermissionRow {
    private Long districtId;
    private String districtName;
    private boolean allowDecrease;
    private String permissionTypeLabel;
}
