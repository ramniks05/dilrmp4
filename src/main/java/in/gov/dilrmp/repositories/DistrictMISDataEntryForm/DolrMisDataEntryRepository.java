package in.gov.dilrmp.repositories.DistrictMISDataEntryForm;

import in.gov.dilrmp.models.dataEntryModel.DolrMisDataEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DolrMisDataEntryRepository extends JpaRepository<DolrMisDataEntry, Long> {

    Optional<DolrMisDataEntry> findByState_Id(Long stateId);

    @Query(value = "SELECT e.state_id, "
            + "COALESCE(e.legacy_dilrmp_sanctioned_pages, 0), "
            + "COALESCE(e.sros_dilrmp_sanctioned, 0) "
            + "FROM dolr_mis_data_entry e", nativeQuery = true)
    List<Object[]> findAllSanctionPairsByStateId();

    @Query(value = "SELECT "
            + "COALESCE(SUM(e.legacy_dilrmp_sanctioned_pages), 0), "
            + "COALESCE(SUM(e.sros_dilrmp_sanctioned), 0) "
            + "FROM dolr_mis_data_entry e", nativeQuery = true)
    List<Object[]> sumSanctionsNational();
}
