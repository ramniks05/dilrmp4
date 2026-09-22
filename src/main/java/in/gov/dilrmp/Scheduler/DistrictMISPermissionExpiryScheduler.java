package in.gov.dilrmp.Scheduler;

import in.gov.dilrmp.services.DataEntryForm.DistrictMISDataEntryPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DistrictMISPermissionExpiryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DistrictMISPermissionExpiryScheduler.class);

    @Autowired
    private DistrictMISDataEntryPermissionService permissionService;

    @Scheduled(cron = "0 0 */6 * * ?")
    public void expireDecreasePermissions() {
        int expiredCount = permissionService.expireDecreasePermissions();
        if (expiredCount > 0) {
            logger.info("Reverted {} district(s) to Increase Only after 24-hour permission expiry.", expiredCount);
        }
    }
}
