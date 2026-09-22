package in.gov.dilrmp.services.physicalProgressServices;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.DistrictMapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.repositories.physicalProgressRepositories.DistrictMapViewRepository;
import in.gov.dilrmp.repositories.physicalProgressRepositories.MapDigitizationReportRepository;
import in.gov.dilrmp.utils.MapDigitizationReportV5Enricher;
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
    @Autowired
    MapDigitizationReportV5Enricher mapDigitizationReportV5Enricher;

    public List<DistrictMapDigitizationReport> getDistrictMAPModelsByStateId(Long stateId) {
        List<DistrictMapDigitizationReport> reports = districtMapViewRepository.findAllByStateId(stateId);
        mapDigitizationReportV5Enricher.enrichDistrictReports(reports, stateId.intValue());
        return reports;
    }

    public List<MapDigitizationReport> getStateListByStateLgdCode(Long lgdCode) {
        List<MapDigitizationReport> reports = mapDigitizationReportRepository.findAllByLgdCode(lgdCode);
        mapDigitizationReportV5Enricher.enrichStateReportListForDistrictPage(reports, lgdCode.intValue());
        return reports;
    }
}
