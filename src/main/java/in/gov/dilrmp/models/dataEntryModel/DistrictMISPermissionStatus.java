package in.gov.dilrmp.models.dataEntryModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictMISPermissionStatus {
    private boolean allowDecrease;
    private LocalDateTime decreaseAllowedUntil;
    private String decreaseAllowedUntilDisplay;
    private boolean permissionJustExpired;

    public static DistrictMISPermissionStatus increaseOnly() {
        return new DistrictMISPermissionStatus(false, null, null, false);
    }

    public static DistrictMISPermissionStatus increaseOnlyExpired() {
        return new DistrictMISPermissionStatus(false, null, null, true);
    }
}
