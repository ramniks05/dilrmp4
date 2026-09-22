package in.gov.dilrmp.repositories.DistrictMISDataEntryForm;

import in.gov.dilrmp.models.dataEntryModel.DistrictMISDataEntryPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DistrictMISDataEntryPermissionRepository extends JpaRepository<DistrictMISDataEntryPermission, Long> {

    Optional<DistrictMISDataEntryPermission> findByDistrictId(Long districtId);

    List<DistrictMISDataEntryPermission> findByDistrict_State_Id(Long stateId);

    List<DistrictMISDataEntryPermission> findByAllowDecreaseTrueAndDecreaseAllowedUntilBefore(LocalDateTime dateTime);
}
