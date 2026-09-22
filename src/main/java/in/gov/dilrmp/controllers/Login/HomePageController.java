package in.gov.dilrmp.controllers.Login;



import in.gov.dilrmp.OnlineVisitor.OnlineVisitorCounter;
import in.gov.dilrmp.models.admin.FlashMessage;
import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.dashboard.DashBordDTO;
import in.gov.dilrmp.models.dataEntryModel.StateRegistrationSystem;

import in.gov.dilrmp.models.naksha.CapacityBuilding;

import in.gov.dilrmp.models.user.Role;
import in.gov.dilrmp.models.user.User;

import in.gov.dilrmp.services.DataEntryForm.IgrMISDataEntryService;
import in.gov.dilrmp.services.administrativeBoundry.VisitorCounterService;
import in.gov.dilrmp.services.naksha.CapacityBuildingService;
import in.gov.dilrmp.services.naksha.GroundTruthingDetailService;
import in.gov.dilrmp.services.naksha.NakshaMISDataService;
import in.gov.dilrmp.services.adminService.MessageService;
import in.gov.dilrmp.services.administrativeBoundry.DistrictService;
import in.gov.dilrmp.services.administrativeBoundry.StateService;
import in.gov.dilrmp.services.dashboard.DashBoardDistrictService;
import in.gov.dilrmp.services.dashboard.DashBoardService;
import in.gov.dilrmp.services.dashboard.DashBoardStateService;
import in.gov.dilrmp.services.naksha.ULBMasterService;
import in.gov.dilrmp.services.user.UserService;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import in.gov.dilrmp.utils.SecurityUtils;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class HomePageController {

    private static String token;
    @Autowired
    DashBoardService dashBoardService;
    @Autowired
    DashBoardStateService dashBoardStateService;
    @Autowired
    DashBoardDistrictService dashBoardDistrictService;

    @Autowired
    IgrMISDataEntryService igrMISDataEntryService;

    @Autowired
    LoogedInUserUtility loogedInUserUtility;
    @Autowired
    private MessageService messageService;
    @Autowired
    DistrictService districtService;
    @Autowired
    StateService stateService;

    @Autowired
    private NakshaMISDataService nakshaMISDataService;

    @Autowired
    private CapacityBuildingService buildingService;
    @Autowired
    private ULBMasterService ulbMasterService;
    @Autowired
    private GroundTruthingDetailService groundTruthingDetailService;
    @Autowired
    private VisitorCounterService visitorCounterService;
    @Autowired
    private  OnlineVisitorCounter onlineVisitorCounter;



    @GetMapping("/")
    public String homePage(Model model, @RequestParam(name = "error", required = false) String error,HttpServletRequest request, HttpServletResponse response,HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        token = SecurityUtils.generateOneTimeToken();
        String userName = auth.getName();
        // Redirect authenticated users to their role home instead of showing public index (avoids extra redirects)
        if (!userName.equals("anonymousUser")) {
            try {
                UserService userService = loogedInUserUtility.getLoggedinUser();
                String roleName = userService.getRole();
                if (roleName != null) {
                    String roleHome = null;
                    if (Role.USER_ROLE_STATE.equals(roleName)) roleHome = "state";
                    else if (Role.USER_ROLE_ULB.equals(roleName)) roleHome = "ulb";
                    else if (Role.USER_ROLE_STATEULB.equals(roleName)) roleHome = "ulb/naksha-home";
                    else if (Role.USER_ROLE_GD.equals(roleName)) roleHome = "ulb/naksha-mis-venderdata/form";
                    else if (Role.USER_ROLE_DISTRICT.equals(roleName)) roleHome = "district";
                    else if (Role.USER_ROLE_ADC.equals(roleName)) roleHome = "adc";
                    else if (Role.USER_ROLE_DOLR.equals(roleName) || Role.USER_ROLE_ADMIN.equals(roleName)) roleHome = "dolr";
                    else if (Role.USER_ROLE_IGR.equals(roleName)) roleHome = "igr";
                    else if (Role.USER_ROLE_SGO.equals(roleName)) roleHome = "sgo/monitor";
                    if (roleHome != null) {
                        return "redirect:/" + roleHome;
                    }
                }
            } catch (Exception ignored) { }
        }
/*
        DashBordDTO nationalDashBordDTOList = dashBoardService.getNationalDashBoardData();
        model.addAttribute("nationalDashBordDTOList",nationalDashBordDTOList);
        List<DashBordDTO> stateDashboardDataList = dashBoardStateService.getStateDashBoardDataByStateIds();
        stateDashboardDataList.sort(Comparator.comparing(DashBordDTO::getName));

        List<DashBordDTO> filteredStateDashboardListTop =dashBoardStateService.filterStatesByTopPercent(stateDashboardDataList);
        model.addAttribute("top5States", filteredStateDashboardListTop);
        List<DashBordDTO> filteredStateDashboardListBottom =dashBoardStateService.filterStatesByBottomPercent(stateDashboardDataList);
        model.addAttribute("bottom5State",filteredStateDashboardListBottom);*/

/*
        model.addAttribute("totalVisitorsFormatted", totalVisitorsFormatted);*/



// Count only once per browser session




        return "index";
    }


    @GetMapping("/getDashboardData")
    public ResponseEntity<?> getDashboardData(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

        // Fetch national dashboard data
        DashBordDTO nationalDashBordDTOList = dashBoardService.getNationalDashBoardData();

        // Fetch state dashboard data
        List<DashBordDTO> stateDashboardDataList = dashBoardStateService.getStateDashBoardDataByStateIds();
        stateDashboardDataList.sort(Comparator.comparing(DashBordDTO::getName));

        // Filter top and bottom 5 states by percentage
        List<DashBordDTO> filteredStateDashboardListTop = dashBoardStateService.getTop5StatesByPercent(stateDashboardDataList);
        session.setAttribute("top5State",filteredStateDashboardListTop);
        List<DashBordDTO> filteredStateDashboardListBottom = dashBoardStateService.getBottom5StatesByPercent(stateDashboardDataList);
        session.setAttribute("bottom5State",filteredStateDashboardListBottom);
        // Prepare response data
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("nationalDashBordDTOList", nationalDashBordDTOList);
        responseMap.put("top5States", filteredStateDashboardListTop);
        responseMap.put("bottom5States", filteredStateDashboardListBottom);

        // Add additional dashboard data
        responseMap.put("revenueCourtsComputerized", nationalDashBordDTOList.getRevenueCourtsComputerized());
        responseMap.put("totalRevenueCourtsPercent", nationalDashBordDTOList.getTotalRevenueCourtsPercent());
        responseMap.put("villagesWith100PercentRorLinkedAadhaar", nationalDashBordDTOList.getVillagesWith100PercentRorLinkedAadhaar());
        responseMap.put("villagesWith100PercentRorLinkedAadhaarPercent", nationalDashBordDTOList.getVillagesWith100PercentRorLinkedAadhaarPercent());
        responseMap.put("totalSro", nationalDashBordDTOList.getTotalSro());
        responseMap.put("sroComputerizedPercent", nationalDashBordDTOList.getSroComputerizedPercent());
        responseMap.put("stateDashboardDataList", stateDashboardDataList);
        session.setAttribute("stateDashboardDataList",stateDashboardDataList);


        // Return the response entity with the dashboard data
        return ResponseEntity.ok(responseMap);
    }
  /*  @GetMapping("/loginPage")
    public ModelAndView loginPage( @RequestParam(name = "error", required = false) String error,HttpServletRequest request) {

            ModelAndView m = new ModelAndView();
             m.setViewName("pages/userprofile/login");
            return m;
    }*/
  @GetMapping("/loginPage")
  public ModelAndView loginPage(Model model, @RequestParam(name = "error", required = false) String error, HttpServletRequest request) {
      //token = SecurityUtils.generateOneTimeToken();
      token ="633eca23d39973d68da8af63962fba9ac2f9aa19920920e9977387d2851b3bdf";
      ModelAndView m = new ModelAndView();
      m.addObject("token",token);
      m.setViewName("pages/userprofile/login");
      return m;
  }
    public static String getOneTimeToken(){
        token ="633eca23d39973d68da8af63962fba9ac2f9aa19920920e9977387d2851b3bdf";
        return token;
    }
    @GetMapping("/state")
    public String loginState(Model model, HttpServletRequest request) {
        UserService userService = loogedInUserUtility.getLoggedinUser();
        Long stateId = userService.getBoundry().getId();
        LocalDate currentDate = LocalDate.now();

        List<String> flashMessages = new ArrayList<>();

        // 1. Global (All State/UTs) message
        FlashMessage globalMessage = messageService.findByStateNull();
        if (isValidMessage(globalMessage, currentDate)) {
            flashMessages.add(globalMessage.getMessage());
        }

        // 2. State-specific message
        FlashMessage stateMessage = messageService.findByStateId(stateId);
        if (isValidMessage(stateMessage, currentDate)) {
            flashMessages.add(stateMessage.getMessage());
        }

        // Add all messages to the model
        if (!flashMessages.isEmpty()) {
            model.addAttribute("flashMessages", flashMessages);
        }

        List<DashBordDTO> stateDashboardDataList = dashBoardStateService.getStateDashBoardDataByStateIds();
        model.addAttribute("stateDashboardDataList", stateDashboardDataList);

        return "pages/state/state_home";
    }

    private boolean isValidMessage(FlashMessage message, LocalDate today) {
        return message != null &&
                message.getStartDate() != null &&
                message.getEndDate() != null &&
                !today.isBefore(message.getStartDate()) &&
                !today.isAfter(message.getEndDate());
    }

    @GetMapping("/district")
    public String loginDistrict(Model model,HttpServletRequest request) {
        UserService userService=loogedInUserUtility.getLoggedinUser();
        Long stateId = districtService.findById(userService.getBoundry().getId()).get().getState().getId();

        LocalDate currentDate = LocalDate.now();

        List<String> flashMessages = new ArrayList<>();

        // 1. Global (All State/UTs) message
        FlashMessage globalMessage = messageService.findByStateNull();
        if (isValidMessage(globalMessage, currentDate)) {
            flashMessages.add(globalMessage.getMessage());
        }

        // 2. State-specific message
        FlashMessage stateMessage = messageService.findByStateId(stateId);
        if (isValidMessage(stateMessage, currentDate)) {
            flashMessages.add(stateMessage.getMessage());
        }

        // Add all messages to the model
        if (!flashMessages.isEmpty()) {
            model.addAttribute("flashMessages", flashMessages);
        }

        List<DashBordDTO> districtdashBoardList = dashBoardDistrictService.getDistrictDashBoardDataByLgdCode();
        model.addAttribute("districtdashBoardList",districtdashBoardList);
        return "pages/district/district_home";
    }

    @GetMapping("/adc")
    public String loginAdc(Model model,HttpServletRequest request) {

        LocalDate currentDate = LocalDate.now();

        List<String> flashMessages = new ArrayList<>();


        FlashMessage globalMessage = messageService.findByStateNull();
        if (isValidMessage(globalMessage, currentDate)) {
            flashMessages.add(globalMessage.getMessage());
        }


        if (!flashMessages.isEmpty()) {
            model.addAttribute("flashMessages", flashMessages);
        }

        List<DashBordDTO> districtdashBoardList =
                dashBoardDistrictService.getDistrictDashBoardDataByLgdCode();
        int totalDistrict = districtdashBoardList != null ? districtdashBoardList.size() : 0;
        int totalTehsil = 0;
        int totalVillage = 0;
        int totalVillagesComputerizationCompleted = 0;
        int totalDigitizedMapsFmbTippans = 0;
        int totalVillagesMap1Generated = 0;
        int totalSroComputerized = 0;
        int totalMrrCompleted = 0;
        int totalVillagesWithUlipn = 0;

        for (DashBordDTO dto : districtdashBoardList) {

            if (dto.getTotalTehsil() != null)
                totalTehsil += dto.getTotalTehsil();

            if (dto.getTotalVillage() != null)
                totalVillage += dto.getTotalVillage();

            if (dto.getVillagesComputerizationCompleted() != null)
                totalVillagesComputerizationCompleted += dto.getVillagesComputerizationCompleted();

            if (dto.getTotalDigitizedMapsFmbTippans() != null)
                totalDigitizedMapsFmbTippans += dto.getTotalDigitizedMapsFmbTippans();

            if (dto.getVillagesMap1Generated() != null)
                totalVillagesMap1Generated += dto.getVillagesMap1Generated();

            if (dto.getSroComputerized() != null)
                totalSroComputerized += dto.getSroComputerized();

            if (dto.getMrrCompleted() != null)
                totalMrrCompleted += dto.getMrrCompleted();

            if (dto.getVillagesWithUlipn() != null)
                totalVillagesWithUlipn += dto.getVillagesWithUlipn();
        }

        model.addAttribute("districtdashBoardList", districtdashBoardList);
        model.addAttribute("totalDistrict", totalDistrict);
        model.addAttribute("totalTehsil", totalTehsil);
        model.addAttribute("totalVillage", totalVillage);
        model.addAttribute("totalVillagesComputerizationCompleted", totalVillagesComputerizationCompleted);
        model.addAttribute("totalDigitizedMapsFmbTippans", totalDigitizedMapsFmbTippans);
        model.addAttribute("totalVillagesMap1Generated", totalVillagesMap1Generated);
        model.addAttribute("totalSroComputerized", totalSroComputerized);
        model.addAttribute("totalMrrCompleted", totalMrrCompleted);
        model.addAttribute("totalVillagesWithUlipn", totalVillagesWithUlipn);

        return "pages/district/adc_home";
    }


    @CrossOrigin(origins = "http://10.1.43.55:80", maxAge = 3600)


    @GetMapping("/dolr")
    public String loginDoLR() {
         return "pages/dolr/dolr_home";
    }


    @GetMapping("/getDolrDashboardData")
    public ResponseEntity<?> getDolrDashboardData(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        try {
            // Fetch national dashboard data
            DashBordDTO nationalDashBordDTOList = dashBoardService.getNationalDashBoardData();

            // Prepare response data
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("totalDistrict", nationalDashBordDTOList.getTotalDistrict());
            responseMap.put("totalTehsil", nationalDashBordDTOList.getTotalTehsil());
            responseMap.put("totalVillage", nationalDashBordDTOList.getTotalVillage());
            responseMap.put("villagesComputerizationCompleted", nationalDashBordDTOList.getVillagesComputerizationCompleted());
            responseMap.put("totalDigitizedMapsFmbTippans", nationalDashBordDTOList.getTotalDigitizedMapsFmbTippans());
            responseMap.put("mrrCompleted", nationalDashBordDTOList.getMrrCompleted());
            responseMap.put("villagesDroneFlyingCompleted", nationalDashBordDTOList.getVillagesDroneFlyingCompleted());
            responseMap.put("revenueCourtsComputerized", nationalDashBordDTOList.getRevenueCourtsComputerized());
            responseMap.put("villagesWith100PercentRorLinkedAadhaar", nationalDashBordDTOList.getVillagesWith100PercentRorLinkedAadhaar());
            responseMap.put("totalSro", nationalDashBordDTOList.getTotalSro());
            responseMap.put("villagesWithUlipn", nationalDashBordDTOList.getVillagesWithUlipn());

            // Return the response entity with the dashboard data
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            // Log the error and return an appropriate response
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching dashboard data.");
        }
    }

    @GetMapping("/igr")
    public String igr(Model model,HttpServletRequest request) {
        Long  stateId = loogedInUserUtility.getLoggedinUser().getIgr().getState().getId();

        LocalDate currentDate = LocalDate.now();

        List<String> flashMessages = new ArrayList<>();

        // 1. Global (All State/UTs) message
        FlashMessage globalMessage = messageService.findByStateNull();
        if (isValidMessage(globalMessage, currentDate)) {
            flashMessages.add(globalMessage.getMessage());
        }

        // 2. State-specific message
        FlashMessage stateMessage = messageService.findByStateId(stateId);
        if (isValidMessage(stateMessage, currentDate)) {
            flashMessages.add(stateMessage.getMessage());
        }

        // Add all messages to the model
        if (!flashMessages.isEmpty()) {
            model.addAttribute("flashMessages", flashMessages);
        }

        StateRegistrationSystem igrMISDataEntry = igrMISDataEntryService.getStateRegistrationSystemByStateId(stateId);

        model.addAttribute("igrMISDataEntry", igrMISDataEntry);
        //List<DashBordDTO> stateDashboardDataList = dashBoardStateService.getStateDashBoardDataByStateIds();
      /*  model.addAttribute("stateDashboardDataList",stateDashboardDataList);*/
        return "pages/igr/igr_home";
    }



    @GetMapping("/getToken")
    @ResponseBody
    public String getToken(HttpServletRequest request) {

        token ="633eca23d39973d68da8af63962fba9ac2f9aa19920920e9977387d2851b3bdf";
        if (token != null) {
            return token; // Return the token if found
        } else {
            return "Token not found"; // Handle case when token is not found
        }
    }



   @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/loginPage?logout"; // Redirect to the login page with a logout parameter
    }

    @RequestMapping(value = "/daysSincePasswordChange", method = RequestMethod.GET)
    public String daysSincePasswordChange(Model model) {
           try {
            model.addAttribute("changepassword", new User());
            return "pages/userprofile/daysSincePasswordChange";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while fetching the password change information.");
           return "redirect:/logout";
        }
    }

    @GetMapping("/ulb")
    public String loginULB(Model model, HttpServletRequest request) {

        return "pages/state/ulb_home";
    }

    @GetMapping("/ulb/naksha-home")
    public String nakshaHome(Model model,HttpSession session) {
        try {
            // Get logged-in user
            UserService userService = loogedInUserUtility.getLoggedinUser();

            CapacityBuilding existingData = buildingService.findByUlbMasterId(userService.getUserID());

            model.addAttribute("existingData", existingData);

            session.setAttribute("userId", userService.getUserID());

            return "pages/naksha/naksha_home";
        } catch (Exception e) {
             model.addAttribute("error", "Something went wrong while loading the Naksha home page.");
            return "error"; // Optional: Create a custom error page if needed
        }
    }


    @RequestMapping(value = "/dashboard-chart", method = RequestMethod.GET)
    public String dashboardChart(Model model) {
        List<State> stateList = stateService.findAllOrderByStateName();
        model.addAttribute("stateList", stateList);
           return "pages/reports/dashboardChart";
    }

    @GetMapping("/getStateData")
    public ResponseEntity<List<DashBordDTO>> getStateDataByStateId(
            @RequestParam("stateId") Long stateId,
            HttpSession session,
            HttpServletRequest request) {

        // Fetch the state dashboard data from the session
        List<DashBordDTO> stateDashboardDataList =
                (List<DashBordDTO>) request.getSession().getAttribute("stateDashboardDataList");

        // If no data exists in session, fetch it from the service and sort by name
        if (stateDashboardDataList == null) {
            stateDashboardDataList = dashBoardStateService.getStateDashBoardDataByStateIds();

            // Optionally, save the data back into the session for future requests
            request.getSession().setAttribute("stateDashboardDataList", stateDashboardDataList);
        }

        // Filter the list based on the provided stateId
        List<DashBordDTO> filteredList = stateDashboardDataList.stream()
                .filter(dto -> Objects.equals(dto.getStateId(), stateId.intValue())) // Null-safe comparison
                .collect(Collectors.toList());

        // Return the filtered data wrapped in ResponseEntity
        return ResponseEntity.ok(filteredList);
    }

    @RequestMapping(value = "/naksha-dashboard", method = RequestMethod.GET)
    public String nakshaDashboard(Model model) {
        nakshaMISDataService.loadMisVendorData();
        ulbMasterService.loadVendorData();
        buildingService.capacityBuildingData();
        groundTruthingDetailService.loadGroundTruthingDetailData();
        int total = nakshaMISDataService.getTotalRoversFromExistingStates();
        model.addAttribute("totalStatesWithExistingRovers", total);
        List<String> stateNamesList = nakshaMISDataService.getDistinctStateNames();
        model.addAttribute("stateNamesList", stateNamesList);
        return "pages/naksha/nakshaDashboard";

    }
    public static String formatToKOrM(long value) {

        if (value >= 1_000_000) {
            return String.format("%.1fM", value / 1_000_000.0)
                    .replace(".0M", "M");
        }

        if (value >= 1_000) {
            return String.format("%.0fK", value / 1_000.0);
        }

        return String.valueOf(value);
    }


    // ✅ Helper method to check date validity






}

