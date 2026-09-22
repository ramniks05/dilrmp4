package in.gov.dilrmp.services.physicalProgressServices;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.DistrictMapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.repositories.physicalProgressRepositories.DistrictMapViewRepository;
import in.gov.dilrmp.repositories.physicalProgressRepositories.MapDigitizationReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class DistrictMAPService {
     private Logger logger = LoggerFactory.getLogger(DistrictMAPService.class);

    @Autowired
    DistrictMapViewRepository districtMapViewRepository;
    @Autowired
    MapDigitizationReportRepository mapDigitizationReportRepository;

    public List<DistrictMapDigitizationReport> getDistrictMAPModelsByStateId(Long stateId) {
        return districtMapViewRepository.findAllByStateId(stateId);
    }

    public List<MapDigitizationReport> getStateListByStateLgdCode(Long lgdCode) {

        return mapDigitizationReportRepository.findAllByLgdCode(lgdCode);
    }
}
