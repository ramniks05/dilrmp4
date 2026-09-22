package in.gov.dilrmp.controllers.rankingReports;
import in.gov.dilrmp.controllers.user.UserProfileController;
import in.gov.dilrmp.models.dashboard.DashBordDTO;
import in.gov.dilrmp.services.dashboard.DashBoardDistrictService;
import in.gov.dilrmp.services.dashboard.DashBoardStateService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("dolr")

public class RankingReportController {

    @Autowired
    DashBoardDistrictService dashBoardDistrictService;
    @Autowired
    DashBoardStateService dashBoardStateService;

    private static final Logger logger = LoggerFactory.getLogger(RankingReportController.class);

    @GetMapping("/district-of-selectable-ranking-report")
    public String showForm(Model model) {
        try {
            // Fetch district dashboard data
            List<DashBordDTO> districtdashBoardList = dashBoardDistrictService.getDistrictDashBoardDataByLgdCode();
            model.addAttribute("districts", districtdashBoardList);

            return "pages/rankingReports/district-progress-filtering-report::district-ranking-filtering-report";
        } catch (Exception e) {
            // Log the exception with proper error message
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("Error occurred while fetching selectable ranking report data", e);

            return "pages/error/error-page::error-page";
        }
    }

    @GetMapping("/district-of-selectable-progress")
    public String gradingDistrictsNewView(Model model) {
        try {
            // Fetch district dashboard data
            List<DashBordDTO> districtdashBoardList = dashBoardDistrictService.getDistrictDashBoardDataByLgdCode();
            model.addAttribute("districts", districtdashBoardList);

            return "pages/rankingReports/district-of-selectable-progress::district-of-selectable-progress";
        } catch (Exception e) {
            // Log the exception
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("Error occurred while fetching district data for selectable progress", e);

            return "pages/error/error-page::error-page";
        }
    }


    @RequestMapping("/grading-states")
    public String gradingStatesView(Model model, HttpSession session) {
        try {
            // Fetch grading data for states
            List<DashBordDTO> gradingOfState = dashBoardStateService.gradingOfState();
            model.addAttribute("gradingOfState", gradingOfState);
            session.setAttribute("gradingOfState", gradingOfState);

            return "pages/rankingReports/grading-of-states::grading-states";
        } catch (Exception e) {
            // Log the exception
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("Error occurred while fetching grading data for states", e);

            return "pages/error/error-page::error-page";
        }
    }

    @RequestMapping("/grading-district")
    public String gradingDistrictView(Model model, HttpSession session) {
        try {
            // Fetch grading data for states
            List<DashBordDTO> gradingOfDistrict = dashBoardDistrictService.gradingOfDistrict();
            model.addAttribute("gradingOfDistrict", gradingOfDistrict);
            session.setAttribute("gradingOfDistrict", gradingOfDistrict);
            return "pages/rankingReports/grading-of-district::grading-district";
        } catch (Exception e) {
            // Log the exception
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("Error occurred while fetching grading data for states", e);

            return "pages/error/error-page::error-page";
        }
    }


    @GetMapping("/state-all-kpi")
    public String stateAllkpiView(Model model, HttpSession session) {
        try {
            // Fetch labels
            Map<String, String> labels = dashBoardDistrictService.createLabels();
            model.addAttribute("labels", labels);

            List<DashBordDTO> reportData = dashBoardStateService.getStateDashBoardDataByStateIds();
            model.addAttribute("reportData", reportData);

            session.setAttribute("reportData", reportData);

            return "pages/rankingReports/state_all_kpi_report::state-all-kpi";
        } catch (Exception e) {
            // Log the exception
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("Error occurred while fetching state KPI data", e);

            return "pages/error/error-page::error-page";
        }
    }


    @GetMapping("/district-all-kpi")
    public String districtAllkpiView(Model model, HttpSession session) {
        try {
            // Fetch labels
            Map<String, String> labels = dashBoardDistrictService.createLabels();
            model.addAttribute("labels", labels);

            List<DashBordDTO> reportData = dashBoardDistrictService.getDistrictDashBoardDataByLgdCode();
            model.addAttribute("reportData", reportData);

            session.setAttribute("reportDataDistrict", reportData);

            return "pages/rankingReports/district_all_kpi_report::district-all-kpi";
        } catch (Exception e) {
             Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("Error occurred while fetching district KPI data", e);

            return "pages/error/error-page::error-page";
        }
    }



}
