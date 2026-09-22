package in.gov.dilrmp.services.physicalProgressServices;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import in.gov.dilrmp.models.reportDTO.clr.DistrictClrReportView;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.repositories.physicalProgressRepositories.DistrictClrViewRepository;
import in.gov.dilrmp.repositories.physicalProgressRepositories.StateClrReportViewRepository;
import in.gov.dilrmp.utils.ClrReportV5Enricher;

@Service
public class DistrictCLRService {

    @Autowired
    DistrictClrViewRepository districtClrViewRepository;
    @Autowired
    StateClrReportViewRepository stateClrReportViewRepository;
    @Autowired
    ClrReportV5Enricher clrReportV5Enricher;

    private Logger logger = LoggerFactory.getLogger(DistrictCLRService.class);

    public List<StateClrReportView> getClrListByStateId(Long  stateId) {
        List<StateClrReportView> reports = stateClrReportViewRepository.findAllByStateId(stateId);
        clrReportV5Enricher.enrichStateReportListForDistrictPage(reports, stateId);
        return reports;
    }

    public List<DistrictClrReportView> getDistrictReportsByStateId(Long stateId) {
        // Fetch data
        List<DistrictClrReportView> clrReportViews = districtClrViewRepository.findAllByStateId(stateId);
        List<StateClrReportView> stateClrReportViews = stateClrReportViewRepository.findAllByStateId(stateId);

        // Map StateClrReportView data for quick access
        Optional<StateClrReportView> stateClrReportViewOptional = stateClrReportViews.stream().findFirst();

        // Update DistrictClrReportView objects with StateClrReportView data
        if (stateClrReportViewOptional.isPresent()) {
            StateClrReportView stateClrReportView = stateClrReportViewOptional.get();
            for (DistrictClrReportView districtView : clrReportViews) {
                districtView.setRorAvailableOnline(stateClrReportView.getRorAvailableOnline());
                districtView.setDigitallySignedRorAvailable(stateClrReportView.getDigitallySignedRorAvailable());
                districtView.setDigitallySignedRorLegallyValid(stateClrReportView.getDigitallySignedRorLegallyValid());
                districtView.setOnlineMutationFacility(stateClrReportView.getOnlineMutationFacility());
                districtView.setAutoTriggerMutation(stateClrReportView.getAutoTriggerMutation());
                districtView.setLandRecordsOnlineFromRegistrationSystem(stateClrReportView.getLandRecordsOnlineFromRegistrationSystem());
                districtView.setRevenueCourtProceedingsPaperless(stateClrReportView.getRevenueCourtProceedingsPaperless());
                districtView.setCaseFilingRedFlaggedInLandRecordsFromCivilCourts(stateClrReportView.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts());
            }
        }

        clrReportV5Enricher.enrichDistrictReports(clrReportViews, stateId);
        return clrReportViews;
    }

}
