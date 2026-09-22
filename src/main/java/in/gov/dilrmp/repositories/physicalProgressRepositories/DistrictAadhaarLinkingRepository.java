package in.gov.dilrmp.repositories.physicalProgressRepositories;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.DistrictLinkedAadhaarViewReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface DistrictAadhaarLinkingRepository extends JpaRepository<DistrictLinkedAadhaarViewReport,Long> {

    List<DistrictLinkedAadhaarViewReport> findAllByStateId(Long stateId);





      @Query(value = "    SELECT\n" +
                   "        COALESCE(s2.state_name, s1.state_name) AS stateName,\n" +
                   "        (s2.total_tehsils - COALESCE(s1.total_tehsils, 0)) AS totalTehsilsDiff,\n" +
                   "        (s2.total_villages - COALESCE(s1.total_villages, 0)) AS totalVillagesDiff,\n" +
                   "        (s2.total_ror - COALESCE(s1.total_ror, 0)) AS totalRorDiff,\n" +
                   "        (s2.villages_clr_completed - COALESCE(s1.villages_clr_completed, 0)) AS villagesClrCompletedDiff,\n" +
                   "        (m2.total_maps_fmb_tippans - COALESCE(m1.total_maps_fmb_tippans, 0)) AS totalMapsFmbTippansDiff,\n" +
                   "        (m2.total_digitized_maps_fmb_tippans - COALESCE(m1.total_digitized_maps_fmb_tippans, 0)) AS totalDigitizedMapsFmbTippansDiff,\n" +
                   "        (m2.villages_linked_with_ror - COALESCE(m1.villages_linked_with_ror, 0)) AS villagesLinkedWithRorDiff,\n" +
                   "        (mr2.mrr_sanctioned - COALESCE(mr1.mrr_sanctioned, 0)) AS mrrSanctionedDiff,\n" +
                   "        (mr2.mrr_completed - COALESCE(mr1.mrr_completed, 0)) AS mrrCompletedDiff,\n" +
                   "        (sr2.villages_final_promulgation_done - COALESCE(sr1.villages_final_promulgation_done, 0)) AS villagesFinalPromulgationDoneDiff,\n" +
                   "        (sro2.number_ofsros_in_state - COALESCE(sro1.number_ofsros_in_state, 0)) AS noOfSroInState,\n" +
                   "        (sro2.number_ofsros_using_online_registration - COALESCE(sro1.number_ofsros_using_online_registration, 0)) AS noOfSroInUsingOnlineRegistration,\n" +
                   "        (s2.ror_computerized - COALESCE(s1.ror_computerized, 0)) AS rorComputerizedDiff,\n" +
                   "        (l2.ror_linked_with_aadhaar - COALESCE(l1.ror_linked_with_aadhaar, 0)) AS rorLinkedWithAadhaarDiff,\n" +
                   "        (s2.districts_with_gender_based_ownership - COALESCE(s1.districts_with_gender_based_ownership, 0)) AS districtsWithGenderBasedOwnershipDiff,\n" +
                   "        (m2.total_cadastral_maps - COALESCE(m1.total_cadastral_maps, 0)) AS totalCadastralMapsDiff,\n" +
                   "        (m2.digitized_cadastral_maps - COALESCE(m1.digitized_cadastral_maps, 0)) AS digitizedCadastralMapsDiff,\n" +
                   "        (m2.villages_georeferenced - COALESCE(m1.villages_georeferenced, 0)) AS villagesGeoreferencedDiff,\n" +
                   "        (m2.total_land_parcels - COALESCE(m1.total_land_parcels, 0)) AS totalLandParcelsDiff,\n" +
                   "        (m2.land_parcels_with_ulipn - COALESCE(m1.land_parcels_with_ulipn, 0)) AS landParcelsWithUlipnDiff,\n" +
                   "        (m2.georeferenced_land_parcels - COALESCE(m1.georeferenced_land_parcels, 0)) AS georeferencedLandParcelsDiff,\n" +
                   "        (r2.revenue_courts_computerized - COALESCE(r1.revenue_courts_computerized, 0)) AS revenueCourtsComputerizedDiff\n" +

                   "    FROM\n" +
                   "        state_clr_report_view_backup s1\n" +
                   "    FULL OUTER JOIN\n" +
                   "        state_clr_report_view_backup s2 ON s1.state_name = s2.state_name AND s2.backup_date = :date2\n" +
                   "    FULL OUTER JOIN\n" +
                   "        map_digitization_report_view_backup m1 ON s1.state_name = m1.state_name AND m1.backup_date = :date1\n" +
                   "    FULL OUTER JOIN\n" +
                   "        map_digitization_report_view_backup m2 ON s2.state_name = m2.state_name AND m2.backup_date = :date2\n" +
                   "    FULL OUTER JOIN\n" +
                   "        mrr_report_view_backup mr1 ON s1.state_name = mr1.state_name AND mr1.backup_date = :date1\n" +
                   "    FULL OUTER JOIN\n" +
                   "        mrr_report_view_backup mr2 ON s2.state_name = mr2.state_name AND mr2.backup_date = :date2\n" +
                   "    FULL OUTER JOIN\n" +
                   "        survey_resurvey_report_view_backup sr1 ON s1.state_name = sr1.state_name AND sr1.backup_date = :date1\n" +
                   "    FULL OUTER JOIN\n" +
                   "        survey_resurvey_report_view_backup sr2 ON s2.state_name = sr2.state_name AND sr2.backup_date = :date2\n" +
                   "    FULL OUTER JOIN\n" +
                   "        state_registration_report_view_backup sro1 ON s1.state_name = sro1.state_name AND sro1.backup_date = :date1\n" +
                   "    FULL OUTER JOIN\n" +
                   "        state_registration_report_view_backup sro2 ON s2.state_name = sro2.state_name AND sro2.backup_date = :date2\n" +

                   "    FULL OUTER JOIN\n" +
                   "        linked_aadhaar_report_view_backup l1 ON s1.state_name = l1.state_name AND l1.backup_date = :date1\n" +
                   "    FULL OUTER JOIN\n" +
                   "        linked_aadhaar_report_view_backup l2 ON s2.state_name = l2.state_name AND l2.backup_date = :date2\n" +

                   "    FULL OUTER JOIN\n" +
                   "        rcms_report_view_backup r1 ON s1.state_name = r1.state_name AND r1.backup_date = :date1\n" +

                   "    FULL OUTER JOIN\n" +
                   "        rcms_report_view_backup r2 ON s2.state_name = r2.state_name AND r2.backup_date = :date2\n" +

                   "    WHERE\n" +
                   "        s1.backup_date = :date1\n" +

                   "    ORDER BY\n" +
                   "        COALESCE(s2.state_name, s1.state_name)\n", nativeQuery = true)
    List<Object[]> findDataDifferences(@Param("date1") LocalDate date1, @Param("date2") LocalDate date2);




    @Query(value = "SELECT\n" +
            "    s.state_name AS stateName,\n" +
            "    s.total_tehsils AS totalTehsils,\n" +
            "    s.total_villages AS totalVillages,\n" +
            "    s.total_ror AS totalRor,\n" +
            "    s.villages_clr_completed AS villagesClrCompleted,\n" +
            "    m.total_maps_fmb_tippans AS totalMapsFmbTippans,\n" +
            "    m.total_digitized_maps_fmb_tippans AS totalDigitizedMapsFmbTippans,\n" +
            "    m.villages_linked_with_ror AS villagesLinkedWithRor,\n" +
            "    mr.mrr_sanctioned AS mrrSanctioned,\n" +
            "    mr.mrr_completed AS mrrCompleted,\n" +
            "    sr.villages_final_promulgation_done AS villagesFinalPromulgationDone,\n" +
            "    sro.number_ofsros_in_state AS noOfSroInState,\n" +
            "    sro.number_ofsros_using_online_registration AS noOfSroOnlineReg,\n" +
            "    s.ror_computerized AS rorComputerized,\n" +
            "    l.ror_linked_with_aadhaar AS rorLinkedWithAadhaar,\n" +
            "    s.districts_with_gender_based_ownership AS districtsWithGenderBasedOwnership,\n" +
            "    m.total_cadastral_maps AS totalCadastralMaps,\n" +
            "    m.digitized_cadastral_maps AS digitizedCadastralMaps,\n" +
            "    m.villages_georeferenced AS villagesGeoreferenced,\n" +
            "    m.total_land_parcels AS totalLandParcels,\n" +
            "    m.land_parcels_with_ulipn AS landParcelsWithUlipn,\n" +
            "    m.georeferenced_land_parcels AS georeferencedLandParcels,\n" +
            "    r.revenue_courts_computerized AS revenueCourtsComputerized\n" +
            "FROM\n" +
            "    state_clr_report_view_backup s\n" +
            "JOIN\n" +
            "    map_digitization_report_view_backup m\n" +
            "    ON s.state_name = m.state_name AND m.backup_date = :backupDate\n" +
            "JOIN\n" +
            "    mrr_report_view_backup mr\n" +
            "    ON s.state_name = mr.state_name AND mr.backup_date = :backupDate\n" +
            "JOIN\n" +
            "    survey_resurvey_report_view_backup sr\n" +
            "    ON s.state_name = sr.state_name AND sr.backup_date = :backupDate\n" +
            "JOIN\n" +
            "    state_registration_report_view_backup sro\n" +
            "    ON s.state_name = sro.state_name AND sro.backup_date = :backupDate\n" +
            "JOIN\n" +
            "    linked_aadhaar_report_view_backup l\n" +
            "    ON s.state_name = l.state_name AND l.backup_date = :backupDate\n" +
            "JOIN\n" +
            "    rcms_report_view_backup r\n" +
            "    ON s.state_name = r.state_name AND r.backup_date = :backupDate\n" +
            "WHERE\n" +
            "    s.backup_date = :backupDate\n" +
            "ORDER BY\n" +
            "    s.state_name\n", nativeQuery = true)
    List<Object[]> findComparisonReportData(@Param("backupDate") LocalDate backupDate);


    @Query(value = "SELECT DISTINCT backup_date FROM state_clr_report_view_backup ORDER BY backup_date DESC", nativeQuery = true)
    List<Date> findDistinctBackupDatesFromStateClrReport();







}
