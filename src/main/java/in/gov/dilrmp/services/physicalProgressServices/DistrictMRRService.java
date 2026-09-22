package in.gov.dilrmp.services.physicalProgressServices;
import in.gov.dilrmp.models.reportDTO.mrr.DistrictMrrViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.repositories.physicalProgressRepositories.DistrictMrrViewRepository;
import in.gov.dilrmp.repositories.physicalProgressRepositories.MRRViewReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DistrictMRRService {

    @Autowired
    DistrictMrrViewRepository districtMrrViewRepository;
    @Autowired
    MRRViewReportRepository mrrViewReportRepository;

    private Logger logger = LoggerFactory.getLogger(DistrictMRRService.class);


    public List<DistrictMrrViewReport> getDistrictReportsByStateId(Long stateId){

        return districtMrrViewRepository.findByStateId(stateId);
    }

    public List<MrrViewReport> getMrrListByStateId(Long stateId) {
        return mrrViewReportRepository.findAllMrrByStateId(stateId);
    }
}
