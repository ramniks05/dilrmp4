package in.gov.dilrmp.services.administrativeBoundry;

import java.util.List;
import java.util.Optional;

import in.gov.dilrmp.models.administrativeBoundry.Adc;
import in.gov.dilrmp.repositories.administrativeBoundry.AdcRepositry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.gov.dilrmp.models.administrativeBoundry.District;
import in.gov.dilrmp.repositories.administrativeBoundry.DistrictRepositry;

@Service
public class DistrictService {

    private static final Logger logger = LoggerFactory.getLogger(DistrictService.class);

    @Autowired
    private DistrictRepositry districtRepositry;
    @Autowired
    private AdcRepositry adcRepositry;



    public List<District> findAllDistrictByStateID(Long state_id) {
        logger.debug("Entering findAllDistrictByStateID with state_id: {}", state_id);

        List<District> districts = districtRepositry.findAllDistrictByStateID(state_id);

        logger.debug("Exiting findAllDistrictByStateID with result: {}", districts);
        return districts;
    }

    public District findBoundryByUserID(Long id) {
        logger.debug("Entering findBoundryByUserID with user ID: {}", id);

        District district = districtRepositry.findDistrictByUserId(id);

        logger.debug("Exiting findBoundryByUserID with result: {}", district);
        return district;
    }

    public Optional<District> findById(Long did) {
        return districtRepositry.findById(did);
    }

    public District findDistrictByUserID(Long userID) {
        logger.info("Finding district for userID: {}", userID);
        District district = districtRepositry.findDistrictByUserId(userID);
        if (district != null) {
            logger.info("District found for userID: {}", userID);
        } else {
            logger.warn("No district found for userID: {}", userID);
        }
        return district;
    }

    public List<District> findDistrictByAdcUserID(Long userID) {
        logger.info("Finding district for userID: {}", userID);
        List<District> districts = districtRepositry.findDistrictByAdcUserId(userID);
        if (districts != null) {
            logger.info("District found for userID: {}", userID);
        } else {
            logger.warn("No district found for userID: {}", userID);
        }
        return districts;
    }


    public Adc findBoundryByAdcUserID(Long id) {
        logger.debug("Entering findBoundryByUserID with user ID: {}", id);

        Adc adc = adcRepositry.findAdcByUserID(id);

        logger.debug("Exiting findBoundryByUserID with result: {}", adc);
        return adc;
    }

    public List<District> findAllDistrictByAdcID(Long loginId) {
        logger.debug("Entering findAllDistrictByStateID with state_id: {}", loginId);

        List<District> districts = districtRepositry.findDistrictByAdcUserId(loginId);

        logger.debug("Exiting findAllDistrictByStateID with result: {}", districts);
        return districts;
    }

}