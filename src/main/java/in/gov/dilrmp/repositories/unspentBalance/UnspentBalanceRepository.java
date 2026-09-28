package in.gov.dilrmp.repositories.unspentBalance;

import in.gov.dilrmp.models.unspentBalance.UnspentBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UnspentBalanceRepository extends JpaRepository<UnspentBalance, Long> {

    List<UnspentBalance> findByStateIdAndAsOnDateOrderByDisplayOrderAsc(Long stateId, LocalDate asOnDate);
}
