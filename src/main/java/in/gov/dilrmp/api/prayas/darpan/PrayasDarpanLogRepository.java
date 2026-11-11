package in.gov.dilrmp.api.prayas.darpan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrayasDarpanLogRepository extends JpaRepository<PrayasDarpanLog, Long> {

    List<PrayasDarpanLog> findTop20ByOrderByIdDesc();

    List<PrayasDarpanLog> findTop200ByOrderByIdDesc();
}
