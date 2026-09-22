package in.gov.dilrmp.services.DataEntryForm;

import in.gov.dilrmp.models.administrativeBoundry.District;
import in.gov.dilrmp.models.dataEntryModel.DistrictMISDataEntryPermission;
import in.gov.dilrmp.models.dataEntryModel.DistrictMISPermissionRow;
import in.gov.dilrmp.models.dataEntryModel.DistrictMISPermissionStatus;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.DistrictMISDataEntryPermissionRepository;
import in.gov.dilrmp.services.administrativeBoundry.DistrictService;
import in.gov.dilrmp.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DistrictMISDataEntryPermissionService {

    public static final int DECREASE_PERMISSION_HOURS = 24;

    @Autowired
    private DistrictMISDataEntryPermissionRepository permissionRepository;

    @Autowired
    private DistrictService districtService;

    public boolean isDecreaseAllowed(Long districtId) {
        return getPermissionStatus(districtId).isAllowDecrease();
    }

    public DistrictMISPermissionStatus getPermissionStatus(Long districtId) {
        if (districtId == null) {
            return DistrictMISPermissionStatus.increaseOnly();
        }
        return permissionRepository.findByDistrictId(districtId)
                .map(this::resolveActivePermission)
                .orElse(DistrictMISPermissionStatus.increaseOnly());
    }

    public List<DistrictMISPermissionRow> getPermissionRowsForState(Long stateId) {
        List<District> districts = districtService.findAllDistrictByStateID(stateId);
        Map<Long, DistrictMISDataEntryPermission> permissionByDistrictId = permissionRepository.findByDistrict_State_Id(stateId).stream()
                .collect(Collectors.toMap(p -> p.getDistrict().getId(), p -> p));

        return districts.stream()
                .map(d -> {
                    DistrictMISDataEntryPermission permission = permissionByDistrictId.get(d.getId());
                    DistrictMISPermissionStatus status = permission != null
                            ? resolveActivePermission(permission)
                            : DistrictMISPermissionStatus.increaseOnly();
                    return new DistrictMISPermissionRow(
                            d.getId(),
                            d.getName(),
                            status.isAllowDecrease(),
                            buildPermissionTypeLabel(status));
                })
                .sorted(Comparator.comparing(DistrictMISPermissionRow::getDistrictName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @Transactional
    public void savePermissionsForState(Long stateId, List<Long> allowDecreaseDistrictIds) {
        Set<Long> allowed = allowDecreaseDistrictIds == null
                ? Collections.emptySet()
                : new HashSet<>(allowDecreaseDistrictIds);

        List<District> districts = districtService.findAllDistrictByStateID(stateId);
        Map<Long, DistrictMISDataEntryPermission> existingByDistrictId = permissionRepository.findByDistrict_State_Id(stateId).stream()
                .collect(Collectors.toMap(p -> p.getDistrict().getId(), p -> p));

        LocalDateTime now = DateUtils.getCurrentLocalDateTime();

        for (District district : districts) {
            DistrictMISDataEntryPermission permission = existingByDistrictId.get(district.getId());
            if (permission == null) {
                permission = new DistrictMISDataEntryPermission();
                permission.setDistrict(district);
            }
            if (allowed.contains(district.getId())) {
                permission.setAllowDecrease(true);
                permission.setDecreaseAllowedUntil(now.plusHours(DECREASE_PERMISSION_HOURS));
            } else {
                permission.setAllowDecrease(false);
                permission.setDecreaseAllowedUntil(null);
            }
            permissionRepository.save(permission);
        }
    }

    @Transactional
    public int expireDecreasePermissions() {
        LocalDateTime now = DateUtils.getCurrentLocalDateTime();
        List<DistrictMISDataEntryPermission> expired = permissionRepository
                .findByAllowDecreaseTrueAndDecreaseAllowedUntilBefore(now);
        for (DistrictMISDataEntryPermission permission : expired) {
            revertToIncreaseOnly(permission);
            permissionRepository.save(permission);
        }
        return expired.size();
    }

    private DistrictMISPermissionStatus resolveActivePermission(DistrictMISDataEntryPermission permission) {
        if (!permission.isAllowDecrease()) {
            return DistrictMISPermissionStatus.increaseOnly();
        }
        if (permission.getDecreaseAllowedUntil() == null
                || DateUtils.getCurrentLocalDateTime().isAfter(permission.getDecreaseAllowedUntil())) {
            revertToIncreaseOnly(permission);
            permissionRepository.save(permission);
            return DistrictMISPermissionStatus.increaseOnlyExpired();
        }
        return new DistrictMISPermissionStatus(
                true,
                permission.getDecreaseAllowedUntil(),
                DateUtils.formatDisplayDateTime(permission.getDecreaseAllowedUntil()),
                false);
    }

    private void revertToIncreaseOnly(DistrictMISDataEntryPermission permission) {
        permission.setAllowDecrease(false);
        permission.setDecreaseAllowedUntil(null);
    }

    private String buildPermissionTypeLabel(DistrictMISPermissionStatus status) {
        if (status.isAllowDecrease()) {
            return "Increase & Decrease (until " + status.getDecreaseAllowedUntilDisplay() + ")";
        }
        return "Increase Only";
    }
}
