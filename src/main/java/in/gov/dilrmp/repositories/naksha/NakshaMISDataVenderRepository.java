package in.gov.dilrmp.repositories.naksha;

import in.gov.dilrmp.models.naksha.NakshaMISDataEntrySolrAndVender;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NakshaMISDataVenderRepository extends JpaRepository<NakshaMISDataEntrySolrAndVender, Long> {


    @Query("SELECT n FROM NakshaMISDataEntrySolrAndVender n WHERE n.ulbMasterId = :ulbMasterId")
    public NakshaMISDataEntrySolrAndVender getVenderByUlbMasterId(@Param("ulbMasterId") Long ulbMasterId);

    // Fetch records by a list of ULB Master IDs
    List<NakshaMISDataEntrySolrAndVender> findByUlbMasterIdIn(List<Long> ulbMasterIds);


    List<NakshaMISDataEntrySolrAndVender> getUlbMasterByUlbMasterId(Long ulbMasterId);

    @Query("SELECT n FROM NakshaMISDataEntrySolrAndVender n ORDER BY n.stateName ASC")
    List<NakshaMISDataEntrySolrAndVender> fetchAllOrderedByStateName();

    @Query("SELECT DISTINCT e.stateName FROM NakshaMISDataEntrySolrAndVender e ORDER BY e.stateName")
    List<String> findDistinctStateNames();

    @Query("SELECT DISTINCT e.ulbName FROM NakshaMISDataEntrySolrAndVender e WHERE e.stateName = :stateName ORDER BY e.ulbName")
    List<String> findGdNameByStateNames(@Param("stateName") String stateName);

    NakshaMISDataEntrySolrAndVender findByUlbName(String ulbName);

    @Query("SELECT  COUNT(DISTINCT n.stateName), COUNT(DISTINCT n.ulbName), SUM(n.bufferAreaDataAcquisition),  SUM(n.sanctionedArea) FROM NakshaMISDataEntrySolrAndVender n  ")
    List<Object[]> getNakshaVenderSummaryJPQL();

//============================== NEW =================================================================================================
        List<NakshaMISDataEntrySolrAndVender> findAll();

   }
