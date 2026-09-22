package in.gov.dilrmp.services.oomf;
import in.gov.dilrmp.models.oomf.OOMFReport;
import in.gov.dilrmp.repositories.oomf.OOMFReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OOMFReportService {

    @Autowired
    OOMFReportRepository oomfReportRepository;


    public OOMFReport saveOomfReportDataEntry(OOMFReport oomfReport) {

        return oomfReportRepository.save(oomfReport);
    }

    public List<String> getFinancialYears() {
        return oomfReportRepository.findDistinctFinancialYears();
    }

    public List<String> getQuarters() {
        return oomfReportRepository.findDistinctQuarters();
    }


    public OOMFReport findByFinancialYearAndQuarter(String financialYear, String quarter) {
        return oomfReportRepository.findByFinancialYearAndQuarter1(financialYear, quarter);
    }


    public List<OOMFReport> getOOMFReportListByYear(String financialYear) {
        return oomfReportRepository.findByFinancialYear(financialYear);
    }

    public List<OOMFReport> getOOMFReportListByYearQuarter(String financialYear, String quarter) {
        return oomfReportRepository.findByFinancialYearAndQuarter(financialYear, quarter);
    }



}
