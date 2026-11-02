package in.gov.dilrmp.services.cna;

import in.gov.dilrmp.models.cna.CnaLgdLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CnaLgdLinkRepository extends JpaRepository<CnaLgdLink, Long> {

    boolean existsByCnaCodeIgnoreCase(String cnaCode);

    Optional<CnaLgdLink> findByCnaCodeIgnoreCase(String cnaCode);
}
