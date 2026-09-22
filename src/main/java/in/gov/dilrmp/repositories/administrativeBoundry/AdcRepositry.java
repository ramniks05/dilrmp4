package in.gov.dilrmp.repositories.administrativeBoundry;

import in.gov.dilrmp.models.administrativeBoundry.Adc;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AdcRepositry extends CrudRepository<Adc, Long> {

    @Query("SELECT s FROM Adc s WHERE s.muser.id = :userId")
    public Adc findAdcByUserID(@Param("userId") Long userId);
}
