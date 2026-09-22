package in.gov.dilrmp.controllers.physicalProgressReports;
import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.services.physicalProgressServices.StateSROService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.*;

@Controller
@RequestMapping("/physicalProgressReports/sro")
public class SROReportController {

@Autowired
StateSROService stateSROService;



    @GetMapping("/state-level")
    public String getSROReportView(Model model, HttpSession session) {
        Map<String, String> labels = stateSROService.createLabels();
        model.addAttribute("labels", labels);
        List<SroReportDTO> sroStateList = stateSROService.getSroStateViwe();
        List<SroReportDTO> grandTotal = stateSROService.getStateSroReportsGrandToatal();
        model.addAttribute("sroStateList", sroStateList);
        model.addAttribute("grandTotal",grandTotal);
        session.setAttribute("sroStateList",sroStateList);
        session.setAttribute("sroGrandTotal",grandTotal);

        return "pages/physicalprogress/state_SRO_ProgressReport";
    }

    @GetMapping("/district-level")
    public String getSRODistrictReportView(@RequestParam("stateId") Long stateId, Model model, HttpSession session) {
        Map<String, String> labels = stateSROService.createLabels();
        model.addAttribute("labels", labels);
        return "pages/physicalprogress/district_SRO_ProgressReport";
    }
}

