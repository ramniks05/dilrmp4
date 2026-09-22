package in.gov.dilrmp.OnlineVisitor;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class OnlineVisitorCounter {

    private final AtomicInteger onlineVisitors = new AtomicInteger(0);

    public void increment() {
        onlineVisitors.incrementAndGet();
    }

    public void decrement() {
        onlineVisitors.decrementAndGet();
    }

    public int getOnlineVisitors() {
        return onlineVisitors.get();
    }
}