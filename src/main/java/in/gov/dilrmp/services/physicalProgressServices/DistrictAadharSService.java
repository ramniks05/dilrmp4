package in.gov.dilrmp.services.physicalProgressServices;

import in.gov.dilrmp.models.reportDTO.linkedaadhaar.DistrictLinkedAadhaarViewReport;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.repositories.physicalProgressRepositories.DistrictAadhaarLinkingRepository;
import in.gov.dilrmp.repositories.physicalProgressRepositories.LinkedAadhaarReportRepository;
import in.gov.dilrmp.utils.AadhaarReportV5Enricher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistrictAadharSService {

    @Autowired
    DistrictAadhaarLinkingRepository districtAadhaarLinkingRepository;
    @Autowired
    LinkedAadhaarReportRepository linkedAadhaarReportRepository;
    @Autowired
    AadhaarReportV5Enricher aadhaarReportV5Enricher;

    private Logger logger = LoggerFactory.getLogger(StateRCMSService.class);


    public List<DistrictLinkedAadhaarViewReport> getDistrictListByStateId(Long stateId){
        List<DistrictLinkedAadhaarViewReport> reports = districtAadhaarLinkingRepository.findAllByStateId(stateId);
        aadhaarReportV5Enricher.enrichDistrictReports(reports, stateId);
        return reports;
    }

    public List<LinkedAadharViewReport> getStateListByStateList(Long stateId){
        List<LinkedAadharViewReport> reports = linkedAadhaarReportRepository.findAllAadhaarByStateId(stateId);
        aadhaarReportV5Enricher.enrichStateReportListForDistrictPage(reports, stateId);
        return reports;
    }

}
