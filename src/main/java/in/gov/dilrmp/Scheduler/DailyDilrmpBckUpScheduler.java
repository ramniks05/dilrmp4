package in.gov.dilrmp.Scheduler;

import in.gov.dilrmp.Scheduler.services.SchedulerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DailyDilrmpBckUpScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DailyDilrmpBckUpScheduler.class);

    @Autowired
    private SchedulerServices schedulerServices;

   /* @Scheduled(fixedRate = 500000000)*/
    // This method will run every night at 1
    @Scheduled(cron = "0 50 23 * * ?")
    public void scheduleTask() {
        try {
            schedulerServices.backupStateClrReportViewData();
        } catch (Exception e) {
            logger.error("Error occurred while backing up State CLR Report View Data", e);
        }

        try {
            schedulerServices.backupLinkedAadharViewData();
        } catch (Exception e) {
            logger.error("Error occurred while backing up Linked Aadhar View Data", e);
        }

        try {
            schedulerServices.backupMapDigitizationViewData();
        } catch (Exception e) {
            logger.error("Error occurred while backing up Map Digitization View Data", e);
        }

        try {
            schedulerServices.backupMrrViewData();
        } catch (Exception e) {
            logger.error("Error occurred while backing up MRR View Data", e);
        }

        try {
            schedulerServices.backupStateRcmsViewData();
        } catch (Exception e) {
            logger.error("Error occurred while backing up State RCMS View Data", e);
        }

        try {
            schedulerServices.backupStateSorViewData();
        } catch (Exception e) {
            logger.error("Error occurred while backing up State SOR View Data", e);
        }

        try {
            schedulerServices.backupStateSurveyResurveyViewData();
        } catch (Exception e) {
            logger.error("Error occurred while backing up State Survey Resurvey View Data", e);
        }

        try {
            schedulerServices.backupDistrictClrReportViewData();
        } catch (Exception e) {
            logger.error("Error occurred while backing up District Linked Aadhaar Report View Data", e);
        }

       try {
            schedulerServices.backupDistrictLinkedAadhaarViewData();
        } catch (Exception e) {
            logger.error("Error occurred while backing up District Linked Aadhaar Report View Data", e);
        }

        try {
            schedulerServices.backupDistrictMrrViewData();
        } catch (Exception e) {
            logger.error("Error occurred while backing up District Mrr Report View Data", e);
        }

        try {
            schedulerServices.backupDistrictRcmViewData();
        } catch (Exception e) {
            logger.error("Error occurred while backing up District RCMS Report View Data", e);
        }

        try {
            schedulerServices.backupDistrictSurveyResurveyViewData();
        } catch (Exception e) {
            logger.error("Error occurred while backing up District Survey Resurvey Report View Data", e);
        }
        try {
            schedulerServices.backupDistrictMapDigitizationViewData();
        } catch (Exception e) {
            logger.error("Error occurred while backing up District Map Digitization View Data", e);
        }
        try {
            schedulerServices.backupDistrictMISDataEntryData();
        } catch (Exception e) {
          logger.error("Error occurred while backing up District MIS Data Entry data", e);
        }
        try {
            schedulerServices.backupStateMISDataEntry();
        } catch (Exception e) {
            logger.error("Error occurred while backing up State MIS Data Entry data", e);
        }
        try {
            schedulerServices.backupStateRegistrationSystem();
        } catch (Exception e) {
            logger.error("Error occurred while backing up State MIS Registration System Data Entry data", e);
        }

        logger.info("Task executed at: {}", System.currentTimeMillis());
    }
}