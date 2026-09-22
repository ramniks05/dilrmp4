package in.gov.dilrmp.services.cna;

import in.gov.dilrmp.models.cna.DilrmpComponentExpenditureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DilrmpComponentExpenditureStatusRepository extends JpaRepository<DilrmpComponentExpenditureStatus, Long> {

    List<DilrmpComponentExpenditureStatus> findByDilrmpPhaseAndStateIdAndFinancialYearOrderBySerialNoAsc(
            String dilrmpPhase, Long stateId, String financialYear);

    void deleteByDilrmpPhaseAndStateIdAndFinancialYear(String dilrmpPhase, Long stateId, String financialYear);
}
