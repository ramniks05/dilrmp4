package in.gov.dilrmp.repositories.admin;
import in.gov.dilrmp.models.admin.FlashMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<FlashMessage, Long> {
    FlashMessage findByStateId(Long stateId);
    @Query("SELECT f FROM FlashMessage f WHERE f.state IS NULL")
    Optional<FlashMessage> findMessageForAllStates();

    @Query("SELECT f FROM FlashMessage f WHERE f.state IS NULL")
    FlashMessage findByStateNull();

}
