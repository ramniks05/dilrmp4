package in.gov.dilrmp.services.DataEntryForm;

import in.gov.dilrmp.models.administrativeBoundry.District;
import in.gov.dilrmp.models.dataEntryModel.DistrictMISDataEntry;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.DistrictMISDataEntryRepository;

import in.gov.dilrmp.services.administrativeBoundry.DistrictService;
import in.gov.dilrmp.utils.DateUtils;
import in.gov.dilrmp.utils.DistrictMISIncreaseOnlyEnforcer;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistrictMISDataEntryService {
    @Autowired
    private DistrictMISDataEntryRepository repository;
    @Autowired
    LoogedInUserUtility loggedInUserUtility;
    @Autowired
    DistrictService districtService;

    @Autowired
    DistrictMISDataEntryPermissionService permissionService;

    Logger logger = LoggerFactory.getLogger(DistrictMISDataEntryService.class);


    public String save(DistrictMISDataEntry entry) {
        try {
            // Ensure the district is properly assigned based on the user's role
            if (loggedInUserUtility.hasRole("ROLE_DISTRICT")) {
                // For district users, set the district from the logged-in user's district
                District userDistrict = districtService.findDistrictByUserID(loggedInUserUtility.getLoggedinUser().getUserID());
                if (userDistrict == null) {
                    throw new IllegalArgumentException("District could not be determined for the logged-in user.");
                }
                entry.setDistrict(userDistrict);  // Override the district in the form
            } else if (loggedInUserUtility.hasRole("ROLE_STATE")) {
                // For state users, ensure district is selected from the form
                if (entry.getDistrict() == null || entry.getDistrict().getId() == null) {
                    throw new IllegalArgumentException("District must be selected.");
                }
            }

            // Now that the district is set, proceed with saving the entry

            // Set the logged-in user
            District dtemp = districtService.findById(entry.getDistrict().getId())
                    .orElseThrow(() -> new IllegalArgumentException("District not found with ID: " + entry.getDistrict().getId()));
            entry.setState(dtemp.getState());
            entry.setUser(loggedInUserUtility.getLoggedinUser().getUser());

            // Set update date (always set)
            entry.setUpdateOnDate(DateUtils.getCurrentLocalDateTime());

            DistrictMISDataEntry existingEntry = entry.getId() != null
                    ? repository.findById(entry.getId()).orElse(null)
                    : repository.findByDistrictId(entry.getDistrict().getId());

            // Set creation date if new entry (id is null)
            if (entry.getId() == null) {
                entry.setCreatedOnDate(DateUtils.getCurrentLocalDateTime());
            } else if (existingEntry != null) {
                entry.setCreatedOnDate(existingEntry.getCreatedOnDate());
            }

            // Manage versioning
            entry.setVersion(entry.getVersion() != null ? entry.getVersion() + 1 : 0);

            // Handle the maps update period date if provided
            if (entry.getMapsUpdatePeriodDate() != null && !entry.getMapsUpdatePeriodDate().equals("")) {
                entry.setMapsUpdatePeriod(DateUtils.parseDate(entry.getMapsUpdatePeriodDate()));
            }

            boolean allowDecrease = permissionService.isDecreaseAllowed(entry.getDistrict().getId());
            int revertedFields = DistrictMISIncreaseOnlyEnforcer.enforce(entry, existingEntry, allowDecrease);
            if (revertedFields > 0) {
                logger.info("Increase-only policy reverted {} field(s) for district ID: {}",
                        revertedFields, entry.getDistrict().getId());
            }

            // Save the entry to the repository
            repository.save(entry);
            logger.info("Successfully saved DistrictMISDataEntry with ID: {}", entry.getId());

        } catch (IllegalArgumentException e) {
            // Handle specific validation errors
            logger.error("Validation error: {}", e.getMessage());
            return "error-message";
        } catch (Exception e) {
            // Log the exception with an appropriate message
            logger.error("Error occurred while saving DistrictMISDataEntry: ", e);
            return "error-message";
        }
        return "success";
    }


    public DistrictMISDataEntry getDistrictData(Long districtId) {
        return repository.findByDistrictId(districtId);
    }

    public boolean isDecreaseAllowed(Long districtId) {
        return permissionService.isDecreaseAllowed(districtId);
    }

}
