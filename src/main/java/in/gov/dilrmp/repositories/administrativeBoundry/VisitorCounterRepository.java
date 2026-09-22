package in.gov.dilrmp.repositories.administrativeBoundry;

import in.gov.dilrmp.OnlineVisitor.VisitorCounter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorCounterRepository
        extends JpaRepository<VisitorCounter, Long> {
}
