package in.gov.dilrmp.controllers.naksha;
import in.gov.dilrmp.models.naksha.CapacityBuilding;
import in.gov.dilrmp.models.naksha.NakshaMISDataEntry;
import in.gov.dilrmp.models.naksha.NakshaMISDataEntrySolrAndVender;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.repositories.naksha.CapacityBuildingRepository;
import in.gov.dilrmp.repositories.naksha.NakshaMISDataRepository;
import in.gov.dilrmp.repositories.naksha.NakshaMISDataVenderRepository;
import in.gov.dilrmp.services.naksha.CapacityBuildingService;
import in.gov.dilrmp.services.naksha.NakshaMISDataService;
import in.gov.dilrmp.utils.NumberFormatterUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("dolr")
public class NaskhaReportController {

    @Autowired
    private NakshaMISDataRepository nakshaMISDataRepository;

    @Autowired
    NakshaMISDataVenderRepository misDataVenderRepository;

    @Autowired
    NakshaMISDataService nakshaMISDataService;
    @Autowired
    private CapacityBuildingRepository capacityBuildingRepository;

    @Autowired
    private CapacityBuildingService capacityBuildingService;

    @GetMapping("/naksha-report")
    public String getNakhsaReportView(Model model, HttpSession session) {
        List<NakshaMISDataEntry> nakshaMISDataEntries = nakshaMISDataRepository.findAllByOrderByState_NameAsc();

        model.addAttribute("nakshaMISDataEntries",nakshaMISDataEntries);
        session.setAttribute("nakshaMISDataEntries",nakshaMISDataEntries);
        List<NakshaMISDataEntry> grandTotal = nakshaMISDataService.getReportsGrandTotal();
        model.addAttribute("grandTotal",grandTotal);
        session.setAttribute("grandTotal",grandTotal);
        return "pages/naksha/state_Naksha_Report::naksha-report";
    }

    @GetMapping("/naksha-report-vender")
    public String getNakhsaVenderReportView(Model model, HttpSession session) {
        List<NakshaMISDataEntrySolrAndVender> nakshaMISDataVenderEntries = misDataVenderRepository.fetchAllOrderedByStateName();
        model.addAttribute("nakshaMISDataEntries",nakshaMISDataVenderEntries);
        session.setAttribute("nakshaMISDataEntries",nakshaMISDataVenderEntries);

        List<NakshaMISDataEntrySolrAndVender> grandTotal = nakshaMISDataService.soiGrandTotal();
        model.addAttribute("grandTotal",grandTotal);
        session.setAttribute("grandTotal",grandTotal);
        return "pages/naksha/state_Naksha_VenderReport::naksha-report-vender";
    }


    @GetMapping("/naksha-report-data-processing")
    public String getNakhsaVenderDataProcessingReportView(Model model, HttpSession session) {
        List<NakshaMISDataEntrySolrAndVender> nakshaMISDataVenderEntries = misDataVenderRepository.fetchAllOrderedByStateName();
        model.addAttribute("nakshaMISDataEntries",nakshaMISDataVenderEntries);
        session.setAttribute("nakshaMISDataEntries",nakshaMISDataVenderEntries);

        List<NakshaMISDataEntrySolrAndVender> grandTotal = nakshaMISDataService.soiDataProcessingGrandTotal();
        model.addAttribute("grandTotal",grandTotal);
        session.setAttribute("grandTotal",grandTotal);
        return "pages/naksha/naksha_Data_Processing_SOI_report::soi-data-processing";
    }


    @GetMapping("/capacity-building")
    public String getCapacityBuildingReportView(Model model, HttpSession session) {
        List<CapacityBuilding> capacityBuilding =  capacityBuildingService.getListCapacityBuildingReport();
        model.addAttribute("capacityBuilding",capacityBuilding);
        session.setAttribute("capacityBuilding",capacityBuilding);

        List<CapacityBuilding> grandTotal = capacityBuildingService.capacityBuildingGrandTotal(capacityBuilding);
        model.addAttribute("grandTotal",grandTotal);
        session.setAttribute("grandTotal",grandTotal);

        return "pages/naksha/capacityBuildingReport::capacity-building";
    }







}
