package in.gov.dilrmp.OnlineVisitor;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

@Component
public class SessionCounterListener implements HttpSessionListener {

    private final VisitorSessionTracker visitorSessionTracker;

    public SessionCounterListener(VisitorSessionTracker visitorSessionTracker) {
        this.visitorSessionTracker = visitorSessionTracker;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // Online count is incremented in VisitorCounterFilter when first session for this visitor is seen
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        Object visitorId = session.getAttribute(VisitorSessionTracker.SESSION_ATTR_VISITOR_ID);
        if (visitorId != null) {
            visitorSessionTracker.sessionDestroyed(visitorId.toString());
        }
    }
}
