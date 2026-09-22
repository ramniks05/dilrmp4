package in.gov.dilrmp.repositories.naksha;
import in.gov.dilrmp.models.naksha.NakshaMISDataEntry;
import in.gov.dilrmp.models.naksha.NakshaMISReportDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NakshaMISDataRepository extends JpaRepository<NakshaMISDataEntry, Long> {

   /* Optional<NakshaMISDataEntry> findByState(State state);*/

    List<NakshaMISDataEntry> findAllByOrderByState_NameAsc();


    @Query("SELECT n FROM NakshaMISDataEntry n WHERE n.ulbMaster.id = :ulbMasterId")
    public NakshaMISDataEntry findByUlbMasterId(@Param("ulbMasterId") Long ulbMasterId);

    @Query("SELECT n FROM NakshaMISDataEntry n WHERE n.muser.id= :muserid")
    public NakshaMISDataEntry findByUlbMuserId(@Param("muserid") Long muserid);

    @Query("SELECT COUNT(e.state.id), " +
            "SUM(e.roversSanctioned), " +
            "SUM(e.roversProcuredForFieldSurvey), " +
            "SUM(e.totalSPMUPositionsSanctioned), " +
            "SUM(e.totalProfessionalsRecruited), " +
            "SUM(e.teamsFormedForsanctioned), " +
            "SUM(e.teamsFormedForFieldSurvey), " +
            "COUNT(e.ulbMaster.id) " +
            "FROM NakshaMISDataEntry e " +
            "WHERE (:stateId IS NULL OR :stateId = 0 OR e.state.id = :stateId)")
    List<Object[]> getRoversSummaryJPQL(@Param("stateId") Long stateId);



    @Query("SELECT new in.gov.dilrmp.models.naksha.NakshaMISReportDTO(" +
            "n.state.id, n.state.name, n.spmuRecruitmentCompleted, n.totalSPMUPositionsSanctioned, " +
            "n.totalProfessionalsRecruited, n.teamsFormedForsanctioned, " +
            "n.teamsFormedForFieldSurvey, n.roversSanctioned, n.roversProcuredForFieldSurvey) " +
            "FROM NakshaMISDataEntry n " +
            "ORDER BY n.state.name")
    List<NakshaMISReportDTO> fetchNakshaMISReport();


    @Query("SELECT e.state.name, " +
            "e.roversSanctioned, " +
            "e.roversProcuredForFieldSurvey, " +
            "e.totalSPMUPositionsSanctioned, " +
            "e.totalProfessionalsRecruited, " +
            "e.teamsFormedForsanctioned, " +
            "e.teamsFormedForFieldSurvey " +
            "FROM NakshaMISDataEntry e " +
            "ORDER BY " +
            "CASE WHEN :metric = 'roversSanctioned' THEN e.roversSanctioned END DESC, " +
            "CASE WHEN :metric = 'roversProcured' THEN e.roversProcuredForFieldSurvey END DESC, " +
            "CASE WHEN :metric = 'totalSPMUPositionsSanctioned' THEN e.totalSPMUPositionsSanctioned END DESC, " +
            "CASE WHEN :metric = 'totalProfessionalsRecruited' THEN e.totalProfessionalsRecruited END DESC, " +
            "CASE WHEN :metric = 'teamsFormedForsanctioned' THEN e.teamsFormedForsanctioned END DESC, " +
            "CASE WHEN :metric = 'teamsFormedForFieldSurvey' THEN e.teamsFormedForFieldSurvey END DESC")
    List<Object[]> getRoversSummaryJPQL(@Param("metric") String metric);



    // 1. Rovers Comparison - orders by roversSanctioned
    @Query("SELECT e.state.name, e.roversSanctioned, e.roversProcuredForFieldSurvey " +
            "FROM NakshaMISDataEntry e " +
            "WHERE e.roversSanctioned IS NOT NULL AND e.roversSanctioned <> 0 " +
            "ORDER BY e.roversSanctioned DESC")
    List<Object[]> getRoversCompare();


    // 2. SPMU Positions Comparison - orders by totalSPMUPositionsSanctioned
    @Query("SELECT e.state.name, e.totalSPMUPositionsSanctioned, e.totalProfessionalsRecruited " +
            "FROM NakshaMISDataEntry e ORDER BY e.totalSPMUPositionsSanctioned DESC")
    List<Object[]> getSPMUPositionsCompare();

    // 3. Teams Formed for Sanctioned Comparison - orders by teamsFormedForsanctioned
    @Query("SELECT e.state.name, e.teamsFormedForsanctioned, e.teamsFormedForFieldSurvey " +
            "FROM NakshaMISDataEntry e ORDER BY e.teamsFormedForsanctioned DESC")
    List<Object[]> getForSanctionedTeamsCompare();


    @Query("SELECT e.state.name, e.roversSanctioned, e.roversProcuredForFieldSurvey, e.totalSPMUPositionsSanctioned, e.totalProfessionalsRecruited, " +
            "e.teamsFormedForsanctioned, e.teamsFormedForFieldSurvey FROM NakshaMISDataEntry e ORDER BY e.state.name ASC")
    List<Object[]> getStateRoversSummary();


    @Query("SELECT e.roversSanctioned, e.roversProcuredForFieldSurvey, e.totalSPMUPositionsSanctioned, e.totalProfessionalsRecruited, " +
            "e.teamsFormedForsanctioned, e.teamsFormedForFieldSurvey, e.nodalDepartmentName FROM NakshaMISDataEntry e WHERE e.muser.id = :muserid")
    List<Object[]> getStateRoversSummary(@Param("muserid") Long muserid);



}
