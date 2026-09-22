package in.gov.dilrmp.repositories.DistrictMISDataEntryForm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gov.dilrmp.models.dataEntryModel.StateMISDataEntry;

import java.util.Optional;

@Repository
public interface StateMISDataEntryRepository extends JpaRepository<StateMISDataEntry,Long> {


    StateMISDataEntry findByState_Id(Long stateID);

    Optional<StateMISDataEntry> findByStateIdAndUserId(Long id, Long id1);
}
