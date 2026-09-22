package in.gov.dilrmp.repositories.naksha;


import in.gov.dilrmp.models.naksha.GroundTruthingDetail;
import in.gov.dilrmp.models.naksha.NakshaMISDataEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GroundTruthingDetailRepository extends JpaRepository<GroundTruthingDetail, Long> {


    @Query("SELECT n FROM GroundTruthingDetail n WHERE n.ulbMaster.id = :ulbMaster")
    List<GroundTruthingDetail> findByUlbMuserId(@Param("ulbMaster") Long ulbMaster);


    @Query("SELECT c.updateOnDate FROM GroundTruthingDetail c WHERE c.ulbMaster.id = :muserId")
    LocalDate findUpdateOnDateByUserId(@Param("muserId") Long muserId);


    Optional<GroundTruthingDetail> findByUlbMaster_Id(Long ulbMasterId);




}
