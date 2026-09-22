package in.gov.dilrmp.repositories.DistrictMISDataEntryForm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.gov.dilrmp.models.dataEntryModel.DistrictMISDataEntry;

import java.util.List;

public interface DistrictMISDataEntryRepository extends JpaRepository<DistrictMISDataEntry, Long> {

   // Method to find by district id
   DistrictMISDataEntry findByDistrictId(Long districtId);

   List<DistrictMISDataEntry> findByStateId(Long stateId);

    //v5 Map Digitization report — aggregate S.No. [11.1] from district MIS data entry
    @Query(value = "SELECT s.lgd_code, COALESCE(SUM(e.total_damaged_missing_maps), 0) "
            + "FROM district_mis_data_entry e "
            + "INNER JOIN state s ON e.state_id = s.id "
            + "GROUP BY s.lgd_code", nativeQuery = true)
    List<Object[]> sumDamagedMissingMapsByStateLgd();

    @Query(value = "SELECT d.id, COALESCE(SUM(e.total_damaged_missing_maps), 0) "
            + "FROM district_mis_data_entry e "
            + "INNER JOIN district d ON e.district_id = d.id "
            + "INNER JOIN state s ON d.state_id = s.id "
            + "WHERE s.lgd_code = :stateLgdCode "
            + "GROUP BY d.id", nativeQuery = true)
    List<Object[]> sumDamagedMissingMapsByDistrictForStateLgd(@Param("stateLgdCode") Integer stateLgdCode);

    @Query("SELECT COALESCE(SUM(e.totalDamagedMissingMaps), 0) FROM DistrictMISDataEntry e")
    Integer sumDamagedMissingMapsNational();

    //v5 CLR report — aggregate S.No. [6.1] RoR with Cadastral Map
    @Query(value = "SELECT e.state_id, COALESCE(SUM(e.ror_with_cadastral_map), 0) "
            + "FROM district_mis_data_entry e "
            + "GROUP BY e.state_id", nativeQuery = true)
    List<Object[]> sumRorWithCadastralMapByStateId();

    @Query(value = "SELECT e.district_id, COALESCE(SUM(e.ror_with_cadastral_map), 0) "
            + "FROM district_mis_data_entry e "
            + "WHERE e.state_id = :stateId "
            + "GROUP BY e.district_id", nativeQuery = true)
    List<Object[]> sumRorWithCadastralMapByDistrictForStateId(@Param("stateId") Long stateId);

    @Query("SELECT COALESCE(SUM(e.rorWithCadastralMap), 0) FROM DistrictMISDataEntry e")
    Integer sumRorWithCadastralMapNational();

    //v5 Aadhaar report — aggregate S.No. [43] RoR linked with Address
    @Query(value = "SELECT e.state_id, COALESCE(SUM(e.ror_linked_with_address), 0) "
            + "FROM district_mis_data_entry e "
            + "GROUP BY e.state_id", nativeQuery = true)
    List<Object[]> sumRorLinkedWithAddressByStateId();

    @Query(value = "SELECT e.district_id, COALESCE(SUM(e.ror_linked_with_address), 0) "
            + "FROM district_mis_data_entry e "
            + "WHERE e.state_id = :stateId "
            + "GROUP BY e.district_id", nativeQuery = true)
    List<Object[]> sumRorLinkedWithAddressByDistrictForStateId(@Param("stateId") Long stateId);

    @Query("SELECT COALESCE(SUM(e.rorLinkedWithAddress), 0) FROM DistrictMISDataEntry e")
    Integer sumRorLinkedWithAddressNational();

    //v5 Aadhaar: Land owners Total / Aadhaar / Mobile / Address aggregates
    // row: [0]=id, [1]=totalLandOwners, [2]=landOwnersAadhar, [3]=mobile, [4]=address
    @Query(value = "SELECT e.state_id, "
            + "COALESCE(SUM(e.total_land_owners), 0), "
            + "COALESCE(SUM(e.land_owners_aadhar), 0), "
            + "COALESCE(SUM(e.land_owners_linked_with_mobile), 0), "
            + "COALESCE(SUM(e.land_owners_linked_with_address), 0) "
            + "FROM district_mis_data_entry e "
            + "GROUP BY e.state_id", nativeQuery = true)
    List<Object[]> sumLandOwnerLinkageByStateId();

    @Query(value = "SELECT e.district_id, "
            + "COALESCE(SUM(e.total_land_owners), 0), "
            + "COALESCE(SUM(e.land_owners_aadhar), 0), "
            + "COALESCE(SUM(e.land_owners_linked_with_mobile), 0), "
            + "COALESCE(SUM(e.land_owners_linked_with_address), 0) "
            + "FROM district_mis_data_entry e "
            + "WHERE e.state_id = :stateId "
            + "GROUP BY e.district_id", nativeQuery = true)
    List<Object[]> sumLandOwnerLinkageByDistrictForStateId(@Param("stateId") Long stateId);

    @Query("SELECT COALESCE(SUM(e.totalLandOwners), 0), COALESCE(SUM(e.landOwnersAadhar), 0), "
            + "COALESCE(SUM(e.landOwnersLinkedWithMobile), 0), COALESCE(SUM(e.landOwnersLinkedWithAddress), 0) "
            + "FROM DistrictMISDataEntry e")
    List<Object[]> sumLandOwnerLinkageNational();
}
