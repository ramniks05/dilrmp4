package in.gov.dilrmp.repositories.administrativeBoundry;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import in.gov.dilrmp.models.administrativeBoundry.District;

public interface DistrictRepositry extends CrudRepository<District, Long> {
    Page<District> findAll(Pageable pageable);

    @Query("SELECT d FROM District d WHERE d.state.lgdCode = :stateLgdCode")
    Page<District> findAll(Pageable pageable, @Param("stateLgdCode") Integer lgdCode);

    List<District> findAll();


    @Query("SELECT d FROM District d WHERE d.state.id = :stateID ORDER BY d.name ASC")
    List<District> findAllDistrictByStateID(@Param("stateID") Long id);

    @Query("SELECT d FROM District d WHERE d.muser.id = :userId")
    public District findDistrictByUserId(@Param("userId") Long userId);

    @Query("SELECT d FROM District d WHERE d.adc.id = :userId")
    List<District> findDistrictByAdcUserId(@Param("userId") Long userId);

    @Query("SELECT s FROM District s order by s.name")
    List<District> districtlist();


}

