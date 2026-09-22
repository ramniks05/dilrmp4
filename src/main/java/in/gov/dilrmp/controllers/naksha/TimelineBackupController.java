package in.gov.dilrmp.controllers.naksha;

import in.gov.dilrmp.services.naksha.TimelineBackupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDate;

@Controller
@RequestMapping("dolr")
public class TimelineBackupController {
    @Autowired
    private TimelineBackupService timelineBackupService;

    private static final Logger logger = LoggerFactory.getLogger(TimelineBackupController.class);

    @RequestMapping(value = "/get-timeline-backup", method = RequestMethod.GET)
    public String resetPassword() {
        return "pages/naksha/timelineBackup::timelineBackup";
    }

    @PostMapping("/timeline-backup")
    public ResponseEntity<String> runTimelineBackup() {
        LocalDate today = LocalDate.now();
        try {
            String result = timelineBackupService.timelineBackup(today);

            if ("already exists".equalsIgnoreCase(result)) {
                String errorMsg = "Backup for today (" + today + ") already exists. Skipping backup.";
                logger.warn(errorMsg);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMsg); // 409 Conflict
            }

            logger.info("Timeline backup executed successfully: {}", result);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            String errorMsg = "Error occurred while executing timeline backup: " + e.getMessage();
            logger.error(errorMsg, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
        }
    }


}
