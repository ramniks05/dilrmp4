package in.gov.dilrmp.repositories.DistrictMISDataEntryForm;

import org.springframework.data.jpa.repository.JpaRepository;

import in.gov.dilrmp.models.dataEntryModel.DistrictMISDataEntry;

import java.util.List;

public interface DistrictMISDataEntryRepository extends JpaRepository<DistrictMISDataEntry, Long> {

   // Method to find by district id
   DistrictMISDataEntry findByDistrictId(Long districtId);

   List<DistrictMISDataEntry> findByStateId(Long stateId);
}
