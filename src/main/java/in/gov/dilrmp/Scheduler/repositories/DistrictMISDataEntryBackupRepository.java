package in.gov.dilrmp.Scheduler.repositories;

import in.gov.dilrmp.Scheduler.models.DistrictMISDataEntryBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DistrictMISDataEntryBackupRepository extends JpaRepository<DistrictMISDataEntryBackup,Long> {

    /**
     * Latest district row on or before the date, plus a current row when that district has no earlier backup.
     * Column order matches DistrictMISDataEntryRepository.sumLegacyDigitizationByStateId().
     */
    @Query(value = "WITH hist AS ( "
            + "SELECT DISTINCT ON (district_id) district_id, state_id, legacy_total_pages, "
            + "legacy_digitised_state_funds_pages, legacy_dilrmp_sanctioned_pages, "
            + "legacy_digitised_dilrmp_funds_pages, legacy_total_digitised_pages, legacy_digitised_upto_year "
            + "FROM districtmisdata_entry_backup WHERE backup_date <= :asOnDate "
            + "ORDER BY district_id, backup_date DESC, id DESC) "
            + "SELECT state_id, COALESCE(SUM(legacy_total_pages), 0), "
            + "COALESCE(SUM(legacy_digitised_state_funds_pages), 0), "
            + "COALESCE(SUM(legacy_dilrmp_sanctioned_pages), 0), "
            + "COALESCE(SUM(legacy_digitised_dilrmp_funds_pages), 0), "
            + "COALESCE(SUM(legacy_total_digitised_pages), 0), "
            + "COALESCE(MAX(legacy_digitised_upto_year), 0) FROM ( "
            + "SELECT district_id, state_id, legacy_total_pages, legacy_digitised_state_funds_pages, "
            + "legacy_dilrmp_sanctioned_pages, legacy_digitised_dilrmp_funds_pages, "
            + "legacy_total_digitised_pages, legacy_digitised_upto_year FROM hist "
            + "UNION ALL "
            + "SELECT e.district_id, e.state_id, e.legacy_total_pages, e.legacy_digitised_state_funds_pages, "
            + "e.legacy_dilrmp_sanctioned_pages, e.legacy_digitised_dilrmp_funds_pages, "
            + "e.legacy_total_digitised_pages, e.legacy_digitised_upto_year "
            + "FROM district_mis_data_entry e "
            + "WHERE CAST(e.last_update_on_date AS date) <= :asOnDate "
            + "AND e.district_id NOT IN (SELECT district_id FROM hist) "
            + ") u GROUP BY state_id", nativeQuery = true)
    List<Object[]> sumLegacyDigitizationByStateAsOn(@Param("asOnDate") LocalDate asOnDate);

    @Query(value = "WITH hist AS ( "
            + "SELECT DISTINCT ON (district_id) district_id, legacy_total_pages, "
            + "legacy_digitised_state_funds_pages, legacy_dilrmp_sanctioned_pages, "
            + "legacy_digitised_dilrmp_funds_pages, legacy_total_digitised_pages, legacy_digitised_upto_year "
            + "FROM districtmisdata_entry_backup WHERE backup_date <= :asOnDate "
            + "ORDER BY district_id, backup_date DESC, id DESC) "
            + "SELECT COALESCE(SUM(legacy_total_pages), 0), "
            + "COALESCE(SUM(legacy_digitised_state_funds_pages), 0), "
            + "COALESCE(SUM(legacy_dilrmp_sanctioned_pages), 0), "
            + "COALESCE(SUM(legacy_digitised_dilrmp_funds_pages), 0), "
            + "COALESCE(SUM(legacy_total_digitised_pages), 0), "
            + "COALESCE(MAX(legacy_digitised_upto_year), 0) FROM ( "
            + "SELECT district_id, legacy_total_pages, legacy_digitised_state_funds_pages, "
            + "legacy_dilrmp_sanctioned_pages, legacy_digitised_dilrmp_funds_pages, "
            + "legacy_total_digitised_pages, legacy_digitised_upto_year FROM hist "
            + "UNION ALL "
            + "SELECT e.district_id, e.legacy_total_pages, e.legacy_digitised_state_funds_pages, "
            + "e.legacy_dilrmp_sanctioned_pages, e.legacy_digitised_dilrmp_funds_pages, "
            + "e.legacy_total_digitised_pages, e.legacy_digitised_upto_year "
            + "FROM district_mis_data_entry e "
            + "WHERE CAST(e.last_update_on_date AS date) <= :asOnDate "
            + "AND e.district_id NOT IN (SELECT district_id FROM hist) "
            + ") u", nativeQuery = true)
    List<Object[]> sumLegacyDigitizationNationalAsOn(@Param("asOnDate") LocalDate asOnDate);

    @Query(value = "WITH hist AS ( "
            + "SELECT DISTINCT ON (district_id) district_id, state_id, revenue_legacy_total_pages, "
            + "revenue_legacy_digitised_state_funds_pages, revenue_legacy_digitised_dilrmp_funds_pages, "
            + "revenue_legacy_digitised_upto_year "
            + "FROM districtmisdata_entry_backup WHERE backup_date <= :asOnDate "
            + "ORDER BY district_id, backup_date DESC, id DESC) "
            + "SELECT state_id, COALESCE(SUM(revenue_legacy_total_pages), 0), "
            + "COALESCE(SUM(revenue_legacy_digitised_state_funds_pages), 0), 0, "
            + "COALESCE(SUM(revenue_legacy_digitised_dilrmp_funds_pages), 0), "
            + "COALESCE(MAX(revenue_legacy_digitised_upto_year), 0) FROM ( "
            + "SELECT district_id, state_id, revenue_legacy_total_pages, revenue_legacy_digitised_state_funds_pages, "
            + "revenue_legacy_digitised_dilrmp_funds_pages, revenue_legacy_digitised_upto_year FROM hist "
            + "UNION ALL "
            + "SELECT e.district_id, e.state_id, e.revenue_legacy_total_pages, "
            + "e.revenue_legacy_digitised_state_funds_pages, e.revenue_legacy_digitised_dilrmp_funds_pages, "
            + "e.revenue_legacy_digitised_upto_year FROM district_mis_data_entry e "
            + "WHERE CAST(e.last_update_on_date AS date) <= :asOnDate "
            + "AND e.district_id NOT IN (SELECT district_id FROM hist) "
            + ") u GROUP BY state_id", nativeQuery = true)
    List<Object[]> sumLegacyRevenueByStateAsOn(@Param("asOnDate") LocalDate asOnDate);

    @Query(value = "WITH hist AS ( "
            + "SELECT DISTINCT ON (district_id) district_id, revenue_legacy_total_pages, "
            + "revenue_legacy_digitised_state_funds_pages, revenue_legacy_digitised_dilrmp_funds_pages, "
            + "revenue_legacy_digitised_upto_year "
            + "FROM districtmisdata_entry_backup WHERE backup_date <= :asOnDate "
            + "ORDER BY district_id, backup_date DESC, id DESC) "
            + "SELECT COALESCE(SUM(revenue_legacy_total_pages), 0), "
            + "COALESCE(SUM(revenue_legacy_digitised_state_funds_pages), 0), 0, "
            + "COALESCE(SUM(revenue_legacy_digitised_dilrmp_funds_pages), 0), "
            + "COALESCE(MAX(revenue_legacy_digitised_upto_year), 0) FROM ( "
            + "SELECT district_id, revenue_legacy_total_pages, revenue_legacy_digitised_state_funds_pages, "
            + "revenue_legacy_digitised_dilrmp_funds_pages, revenue_legacy_digitised_upto_year FROM hist "
            + "UNION ALL "
            + "SELECT e.district_id, e.revenue_legacy_total_pages, e.revenue_legacy_digitised_state_funds_pages, "
            + "e.revenue_legacy_digitised_dilrmp_funds_pages, e.revenue_legacy_digitised_upto_year "
            + "FROM district_mis_data_entry e "
            + "WHERE CAST(e.last_update_on_date AS date) <= :asOnDate "
            + "AND e.district_id NOT IN (SELECT district_id FROM hist) "
            + ") u", nativeQuery = true)
    List<Object[]> sumLegacyRevenueNationalAsOn(@Param("asOnDate") LocalDate asOnDate);

    @Query(value = "WITH hist AS ( "
            + "SELECT DISTINCT ON (district_id) district_id, state_id, totalsro, sro_computerized, "
            + "sros_modernised_state_funds, sros_modernised_dilrmp_funds "
            + "FROM districtmisdata_entry_backup WHERE backup_date <= :asOnDate "
            + "ORDER BY district_id, backup_date DESC, id DESC) "
            + "SELECT state_id, COALESCE(SUM(totalsro), 0), COALESCE(SUM(sro_computerized), 0), "
            + "COALESCE(SUM(sros_modernised_state_funds), 0), COALESCE(SUM(sros_modernised_dilrmp_funds), 0) FROM ( "
            + "SELECT district_id, state_id, totalsro, sro_computerized, sros_modernised_state_funds, "
            + "sros_modernised_dilrmp_funds FROM hist "
            + "UNION ALL "
            + "SELECT e.district_id, e.state_id, e.total_sro, e.sro_computerized, e.sros_modernised_state_funds, "
            + "e.sros_modernised_dilrmp_funds FROM district_mis_data_entry e "
            + "WHERE CAST(e.last_update_on_date AS date) <= :asOnDate "
            + "AND e.district_id NOT IN (SELECT district_id FROM hist) "
            + ") u GROUP BY state_id", nativeQuery = true)
    List<Object[]> sumSroModernizationByStateAsOn(@Param("asOnDate") LocalDate asOnDate);

    @Query(value = "WITH hist AS ( "
            + "SELECT DISTINCT ON (district_id) district_id, totalsro, sro_computerized, "
            + "sros_modernised_state_funds, sros_modernised_dilrmp_funds "
            + "FROM districtmisdata_entry_backup WHERE backup_date <= :asOnDate "
            + "ORDER BY district_id, backup_date DESC, id DESC) "
            + "SELECT COALESCE(SUM(totalsro), 0), COALESCE(SUM(sro_computerized), 0), "
            + "COALESCE(SUM(sros_modernised_state_funds), 0), COALESCE(SUM(sros_modernised_dilrmp_funds), 0) FROM ( "
            + "SELECT district_id, totalsro, sro_computerized, sros_modernised_state_funds, "
            + "sros_modernised_dilrmp_funds FROM hist "
            + "UNION ALL "
            + "SELECT e.district_id, e.total_sro, e.sro_computerized, e.sros_modernised_state_funds, "
            + "e.sros_modernised_dilrmp_funds FROM district_mis_data_entry e "
            + "WHERE CAST(e.last_update_on_date AS date) <= :asOnDate "
            + "AND e.district_id NOT IN (SELECT district_id FROM hist) "
            + ") u", nativeQuery = true)
    List<Object[]> sumSroModernizationNationalAsOn(@Param("asOnDate") LocalDate asOnDate);
}
