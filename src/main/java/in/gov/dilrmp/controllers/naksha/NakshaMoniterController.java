package in.gov.dilrmp.controllers.naksha;

import in.gov.dilrmp.models.naksha.NakshaMisStateDTO;
import in.gov.dilrmp.models.user.User;
import in.gov.dilrmp.services.naksha.NakshaMISDataService;
import in.gov.dilrmp.services.user.UserService;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("sgo")
public class NakshaMoniterController {

    @Autowired
    LoogedInUserUtility loogedInUserUtility;
    @Autowired
    private NakshaMISDataService nakshaMISDataService;
    private static final Logger logger = LoggerFactory.getLogger(NakshaMoniterController.class);

    @GetMapping("/monitor")
    public String showNakhsaVenderMonitor(Model model) {
        try {
            UserService userService = loogedInUserUtility.getLoggedinUser();
            User user = userService.getUser();

            // Fetch states and zone name
            List<NakshaMisStateDTO> nakshaMisStateDTOList = nakshaMISDataService.findStates();
            /* String gdName = nakshaMISDataService.getGdNameByZoneId(user.getId().intValue());*/

            /* model.addAttribute("zoneName", gdName);*/
            model.addAttribute("states", nakshaMisStateDTOList);
            /* model.addAttribute("nakshaMISDataEntry", new NakshaMISDataEntrySolrAndVender());*/

            logger.info("Successfully loaded Naksha Vendor Form for user ID: {}", user.getId());

            return "pages/naksha/naksha_data_entry_monitor";
        } catch (Exception e) {
            logger.error("Error occurred while loading Naksha Vendor Form", e);
            return "error_page";
        }
    }

}
