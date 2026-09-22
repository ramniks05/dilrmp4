package in.gov.dilrmp.OnlineVisitor;

import in.gov.dilrmp.services.administrativeBoundry.VisitorCounterService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import java.io.IOException;

@Component
public class VisitorCounterFilter implements Filter {

    private final VisitorCounterService visitorCounterService;
    private final OnlineVisitorCounter onlineVisitorCounter;
    private final VisitorSessionTracker visitorSessionTracker;

    public VisitorCounterFilter(VisitorCounterService visitorCounterService,
                                OnlineVisitorCounter onlineVisitorCounter,
                                VisitorSessionTracker visitorSessionTracker) {
        this.visitorCounterService = visitorCounterService;
        this.onlineVisitorCounter = onlineVisitorCounter;
        this.visitorSessionTracker = visitorSessionTracker;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession ses = req.getSession(true);

        // Total visitors: count once per browser session (first visit)
        if (ses.getAttribute("VISITOR_COUNTED") == null) {
            visitorCounterService.increment();
            ses.setAttribute("VISITOR_COUNTED", true);
        }

        // Online visitors: one per browser (cookie), count only on first session for that browser
        String visitorId = getOrCreateVisitorId(req, res);
        if (ses.getAttribute(VisitorSessionTracker.SESSION_ATTR_VISITOR_ID) == null) {
            ses.setAttribute(VisitorSessionTracker.SESSION_ATTR_VISITOR_ID, visitorId);
            visitorSessionTracker.sessionCreated(visitorId);
        }

        // Expose totals in session for UI
        long totalVisitors = visitorCounterService.getTotalVisitors();
        String totalVisitorsFormatted = formatIndianNumber(totalVisitors);
        ses.setAttribute("totalVisitors", totalVisitors);
        ses.setAttribute("totalVisitorsFormatted", totalVisitorsFormatted);
        ses.setAttribute("onlineVisitors", onlineVisitorCounter.getOnlineVisitors());

        chain.doFilter(request, response);
    }

    private String getOrCreateVisitorId(HttpServletRequest req, HttpServletResponse res) {
        Cookie[] cookies = req.getCookies();
        Optional<String> existing = cookies == null ? Optional.empty()
                : Arrays.stream(cookies)
                        .filter(c -> VisitorSessionTracker.COOKIE_NAME.equals(c.getName()))
                        .map(Cookie::getValue)
                        .findFirst();
        if (existing.isPresent() && !existing.get().isEmpty()) {
            return existing.get();
        }
        String newId = VisitorSessionTracker.newVisitorId();
        Cookie cookie = new Cookie(VisitorSessionTracker.COOKIE_NAME, newId);
        cookie.setMaxAge(24 * 60 * 60); // 24 hours
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        res.addCookie(cookie);
        return newId;
    }

    // ---- helper method ----
    private String formatToKOrM(long value) {
        if (value >= 1_000_000) {
            return String.format("%.1fM", value / 1_000_000.0);
        } else if (value >= 1_000) {
            return String.format("%.1fK", value / 1_000.0);
        }
        return String.valueOf(value);
    }

    public String formatIndianNumber(long value) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "IN"));
        return nf.format(value);
    }
}

