package in.gov.dilrmp.services.physicalProgressServices;

import in.gov.dilrmp.models.reportDTO.linkedaadhaar.DistrictLinkedAadhaarViewReport;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.repositories.physicalProgressRepositories.DistrictAadhaarLinkingRepository;
import in.gov.dilrmp.repositories.physicalProgressRepositories.LinkedAadhaarReportRepository;
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

    private Logger logger = LoggerFactory.getLogger(StateRCMSService.class);


    public List<DistrictLinkedAadhaarViewReport> getDistrictListByStateId(Long stateId){

        return districtAadhaarLinkingRepository.findAllByStateId(stateId);
    }

    public List<LinkedAadharViewReport> getStateListByStateList(Long stateId){

        return linkedAadhaarReportRepository.findAllAadhaarByStateId(stateId);
    }

}
