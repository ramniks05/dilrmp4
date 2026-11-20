package in.gov.dilrmp.Scheduler;

import in.gov.dilrmp.api.prayas.darpan.PrayasDarpanPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Auto-push live PRAYAS KPIs to DARPAN on the 1st of every month (06:00 server time).
 */
@Component
public class PrayasDarpanPushScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PrayasDarpanPushScheduler.class);

    private final PrayasDarpanPushService pushService;

    public PrayasDarpanPushScheduler(PrayasDarpanPushService pushService) {
        this.pushService = pushService;
    }

    @Scheduled(cron = "0 0 6 1 * ?")
    public void pushOnFirstOfMonth() {
        try {
            logger.info("Starting scheduled PRAYAS DARPAN monthly push");
            Map<String, Object> result = pushService.pushNow("scheduler");
            logger.info("Scheduled PRAYAS DARPAN push finished: {}", result.get("message"));
        } catch (Exception e) {
            logger.error("Scheduled PRAYAS DARPAN push failed", e);
        }
    }
}
