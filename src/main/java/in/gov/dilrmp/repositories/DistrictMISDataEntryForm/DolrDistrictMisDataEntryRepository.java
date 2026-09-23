package in.gov.dilrmp.repositories.DistrictMISDataEntryForm;

import in.gov.dilrmp.models.dataEntryModel.DolrDistrictMisDataEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DolrDistrictMisDataEntryRepository extends JpaRepository<DolrDistrictMisDataEntry, Long> {

    Optional<DolrDistrictMisDataEntry> findByDistrict_Id(Long districtId);

    /** [0]=stateId, [1]=legacy pages, [2]=revenue pages */
    @Query(value = "SELECT e.state_id, "
            + "COALESCE(SUM(e.legacy_dilrmp_sanctioned_pages), 0), "
            + "COALESCE(SUM(e.revenue_legacy_dilrmp_sanctioned_pages), 0) "
            + "FROM dolr_district_mis_data_entry e "
            + "GROUP BY e.state_id", nativeQuery = true)
    List<Object[]> sumSanctionsByStateId();

    /** [0]=districtId, [1]=legacy pages, [2]=revenue pages */
    @Query(value = "SELECT e.district_id, "
            + "COALESCE(e.legacy_dilrmp_sanctioned_pages, 0), "
            + "COALESCE(e.revenue_legacy_dilrmp_sanctioned_pages, 0) "
            + "FROM dolr_district_mis_data_entry e "
            + "WHERE e.state_id = :stateId", nativeQuery = true)
    List<Object[]> findSanctionsByStateId(@Param("stateId") Long stateId);

    /** [0]=legacy pages, [1]=revenue pages */
    @Query(value = "SELECT "
            + "COALESCE(SUM(e.legacy_dilrmp_sanctioned_pages), 0), "
            + "COALESCE(SUM(e.revenue_legacy_dilrmp_sanctioned_pages), 0) "
            + "FROM dolr_district_mis_data_entry e", nativeQuery = true)
    List<Object[]> sumSanctionsNational();
}
