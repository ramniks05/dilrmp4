package in.gov.dilrmp.services.naksha;
import in.gov.dilrmp.models.naksha.IECActivities;
import in.gov.dilrmp.repositories.naksha.IecActivityRepository;
import in.gov.dilrmp.repositories.administrativeBoundry.StateRepositry;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Service
public class IecActivityService {

    private static final Logger logger = LoggerFactory.getLogger(IecActivityService.class);

    @Autowired
    private IecActivityRepository repository;

    @Autowired
    private StateRepositry stateRepositry;

    @Autowired
    private HttpSession session;

    /**
     * Finds an existing IEC entry by ULB user ID
     */
    public IECActivities findByUlbMasterId(Long ulbMasterID) {
        if (ulbMasterID == null) {
            logger.warn("ULB ID is null in findByUlbMasterId");
            return null;
        }
        return repository.findByUlbMasterId(ulbMasterID);
    }

    /**
     * Saves or updates IEC activity
     */
    public String saveIecActivity(IECActivities iecActivity) {
        try {
            // Set updated date
            iecActivity.setUpdateOnDate(LocalDate.now());

            repository.save(iecActivity);

            logger.info("IEC Activity saved successfully for user ID {}",
                    iecActivity.getMuser() != null ? iecActivity.getMuser().getId() : "Unknown");
            return "success";

        } catch (Exception e) {
            logger.error("Error while saving IEC activity", e);
            return "failure";
        }
    }

    /**
     * Gets the last updated date of the IEC entry
     */
    public LocalDate getUpdateOnDateByUserId(Long userId) {
        try {
            return repository.findUpdateOnDateByUserId(userId);
        } catch (Exception e) {
            logger.error("Failed to fetch updateOnDate for userId={}", userId, e);
            return null;
        }
    }

    public List<IECActivities> findAll() {
        try {
             return repository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching IEC activities", e);
            throw e; // rethrow or wrap in custom exception if needed
        }
    }

}
