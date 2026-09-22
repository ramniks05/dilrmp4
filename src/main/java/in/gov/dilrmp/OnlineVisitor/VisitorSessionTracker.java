package in.gov.dilrmp.OnlineVisitor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

/**
 * Tracks online visitors globally: one count per browser (via cookie),
 * so multiple tabs from the same browser count as one online visitor.
 */
@Component
public class VisitorSessionTracker {

    public static final String COOKIE_NAME = "OV_ID";
    public static final String SESSION_ATTR_VISITOR_ID = "ONLINE_VISITOR_ID";

    private final OnlineVisitorCounter onlineVisitorCounter;
    private final Map<String, AtomicInteger> visitorSessionCount = new ConcurrentHashMap<>();

    public VisitorSessionTracker(OnlineVisitorCounter onlineVisitorCounter) {
        this.onlineVisitorCounter = onlineVisitorCounter;
    }

    /**
     * Call when a new session is created for this visitor.
     * Increments global online count only the first time this visitor has a session.
     */
    public void sessionCreated(String visitorId) {
        if (visitorId == null || visitorId.isEmpty()) return;
        int previous = visitorSessionCount
                .computeIfAbsent(visitorId, k -> new AtomicInteger(0))
                .getAndIncrement();
        if (previous == 0) {
            onlineVisitorCounter.increment();
        }
    }

    /**
     * Call when a session is destroyed.
     * Decrements global online count when the last session for this visitor ends.
     */
    public void sessionDestroyed(String visitorId) {
        if (visitorId == null || visitorId.isEmpty()) return;
        AtomicInteger count = visitorSessionCount.get(visitorId);
        if (count == null) return;
        int after = count.decrementAndGet();
        if (after <= 0) {
            visitorSessionCount.remove(visitorId);
            if (after == 0) {
                onlineVisitorCounter.decrement();
            }
        }
    }

    public static String newVisitorId() {
        return UUID.randomUUID().toString();
    }
}
