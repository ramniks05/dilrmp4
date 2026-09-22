package in.gov.dilrmp.services.administrativeBoundry;

import in.gov.dilrmp.OnlineVisitor.VisitorCounter;
import in.gov.dilrmp.repositories.administrativeBoundry.VisitorCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service

public class VisitorCounterService {

    @Autowired
    private VisitorCounterRepository repository;


    public void increment() {
        VisitorCounter counter =
                repository.findById(1L)
                        .orElse(new VisitorCounter());

        counter.setId(1L);
        counter.setTotalVisitors(
                counter.getTotalVisitors() == null
                        ? 1
                        : counter.getTotalVisitors() + 1
        );

        repository.save(counter);
    }

    public long getTotalVisitors() {
        return repository.findById(1L)
                .map(VisitorCounter::getTotalVisitors)
                .orElse(0L);
    }
}

