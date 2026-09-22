package in.gov.dilrmp.services.physicalProgressServices;
import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.SurveyResurveyViewReport;
import in.gov.dilrmp.repositories.physicalProgressRepositories.SurveyResurveyviewRepository;
import in.gov.dilrmp.utils.NumberFormatterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StateSurveyService {


    @Autowired
    SurveyResurveyviewRepository surveyResurveyviewRepository;

    private Logger logger = LoggerFactory.getLogger(StateSurveyService.class);

    DecimalFormat df = new DecimalFormat("#.####");

    public Map<String, String> createLabels() {
        Map<String, String> labels = new HashMap<>();
        labels.put("serialNumber", ReportLabels.SERIAL_NUMBER);
        labels.put("stateUT", ReportLabels.STATE_UT);
        labels.put("totalState", ReportLabels.TOTAL_STATE_UT);
        labels.put("districtName", ReportLabels.DISTRICT_NAME);
        labels.put("totalDistricts", ReportLabels.TOTAL_DISTRICTS);
        labels.put("totalTehsils", ReportLabels.TOTAL_TEHSILS);
        labels.put("districtName", ReportLabels.DISTRICT_NAME);
        labels.put("totalVillages", ReportLabels.TOTAL_VILLAGES);
        labels.put("totalRuralRevenueAreaSqKm", ReportLabels.TOTAL_RURAL_REVENUE_AREA_SQ_KM);
        labels.put("areaSanctionedForSurveyResurveySqKm", ReportLabels.AREA_SANCTIONED_FOR_SURVEY_RE_SURVEY_SQ_KM);
        labels.put("surveyCompleted", ReportLabels.SURVEY_COMPLETED);
        labels.put("ongoingSurvey", ReportLabels.ONGOING_SURVEY);
        labels.put("notstarted", ReportLabels.NOT_STARTED);
        labels.put("dron", ReportLabels.DRON_SURVEY);
        labels.put("dronFlyingCompeted", ReportLabels.DRON_FLYING_COMPLETED);
        labels.put("areaOfVillagesWhereSuveySanctionedNotStarted", ReportLabels.AREA_OF_VILLAGES_WHERE_SURVEY_SANCTIONED_NOT_STARTED);
        labels.put("areaOfVillagesSqKM", ReportLabels.AREA_OF_VILLAGES_SQ_KM);
        labels.put("Map1IsGenerated", ReportLabels.MAP_1_IS_GENERATED);
        labels.put("Draftmappublishedobjectionsinvited", ReportLabels.DRAFT_MAP_PUBLISHED);
        labels.put("finalPromulgationDone", ReportLabels.FINAL_PROMULGATION_DONE);
        labels.put("surveySanctionedButNotStarted", ReportLabels.SURVEY_SANCTIONED_NOT_STARTED);
        labels.put("numberOfVillages", ReportLabels.NUMBER_OF_VILLAGES);
        labels.put("areaSqKm", ReportLabels.AREA_SQ_KM);
        labels.put("draftRevenueMapsGenerated", ReportLabels.DRAFT_REVENUE_MAPS_GENERATED);
        labels.put("finalPromulgationDoneIncludingPublicationOfFinalMaps", ReportLabels.FINAL_PROMULGATION_DONE_INCLUDING_PUBLICATION_OF_FINAL_MAPS);
        labels.put("surveyOngoing", ReportLabels.SURVEY_ONGOING);
        labels.put("number", ReportLabels.NO);
        labels.put("percentage", ReportLabels.PERCENTAGE);
        labels.put("total", ReportLabels.TOTAL);
        labels.put("reportName", ReportLabels.SURVEY_REPORT);
        labels.put("grandTotal", ReportLabels.GRAND_TOTAL);
        return labels;
    }


//    public  List<SurveyResurveyViewReport> getStateSurveyReportsGrandToatal(){
//        DecimalFormat df = new DecimalFormat("#.####");
//        return surveyResurveyviewRepository.findAllSurveyByStateId(999);
//    }
public List<SurveyResurveyViewReport> getStateSurveyReportsGrandToatal() {
    DecimalFormat df = new DecimalFormat("#.####");
    List<SurveyResurveyViewReport> surveyList = surveyResurveyviewRepository.findAllSurveyByStateId(999);
    surveyList = surveyList.stream()
            .map(survey -> {
                survey.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(survey.getTotalDistrict()));
                survey.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(survey.getTotalTehsils()));
                survey.setFormattingTotalVillages(NumberFormatterUtil.formatWithCommas(survey.getTotalVillages()));
                survey.setFormattingvillagesDroneFlyingCompleted(NumberFormatterUtil.formatWithCommas(survey.getVillagesDroneFlyingCompleted()));
                survey.setFormattingvillagesMap1Generated(NumberFormatterUtil.formatWithCommas(survey.getVillagesMap1Generated()));
                survey.setFormattingvillagesDraftMapPublished(NumberFormatterUtil.formatWithCommas(survey.getVillagesDraftMapPublished()));
                survey.setFormattingvillagesFinalPromulgationDone(NumberFormatterUtil.formatWithCommas(survey.getVillagesFinalPromulgationDone()));
                survey.setFormattingvillagesSurveySanctionNotStarted(NumberFormatterUtil.formatWithCommas(survey.getVillagesSurveySanctionNotStarted()));

                survey.setFormatetotalRuralRevenueArea(formatBigDecimal(survey.getTotalRuralRevenueArea(), df));
                survey.setFomateareaSanctionedForSurvey(formatBigDecimal(survey.getAreaSanctionedForSurvey(), df));
                survey.setFormateareaDroneFlyingCompleted(formatBigDecimal(survey.getAreaDroneFlyingCompleted(), df));
                survey.setFormateareaSurveySanctionNotStarted(formatBigDecimal(survey.getAreaSurveySanctionNotStarted(), df));
                return survey;
            })
            .collect(Collectors.toList());
    return surveyList;
}
    public  List<SurveyResurveyViewReport>  getAllSurveyResurveyList(){
        List<SurveyResurveyViewReport> surveyList = surveyResurveyviewRepository.findAll(Sort.by(Sort.Direction.ASC, "stateName"));
        surveyList = surveyList.stream()
                .filter(survey -> !survey.getStateId().equals(Long.valueOf(999)))
                .map(survey -> {
                    survey.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(survey.getTotalDistrict()));
                    survey.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(survey.getTotalTehsils()));
                    survey.setFormattingTotalVillages(NumberFormatterUtil.formatWithCommas(survey.getTotalVillages()));
                    survey.setFormattingvillagesDroneFlyingCompleted(NumberFormatterUtil.formatWithCommas(survey.getVillagesDroneFlyingCompleted()));
                    survey.setFormattingvillagesMap1Generated(NumberFormatterUtil.formatWithCommas(survey.getVillagesMap1Generated()));
                    survey.setFormattingvillagesDraftMapPublished(NumberFormatterUtil.formatWithCommas(survey.getVillagesDraftMapPublished()));
                    survey.setFormattingvillagesFinalPromulgationDone(NumberFormatterUtil.formatWithCommas(survey.getVillagesFinalPromulgationDone()));
                    survey.setFormattingvillagesSurveySanctionNotStarted(NumberFormatterUtil.formatWithCommas(survey.getVillagesSurveySanctionNotStarted()));

                    survey.setFormatetotalRuralRevenueArea(formatBigDecimal(survey.getTotalRuralRevenueArea(), df));
                    survey.setFomateareaSanctionedForSurvey(formatBigDecimal(survey.getAreaSanctionedForSurvey(), df));
                    survey.setFormateareaDroneFlyingCompleted(formatBigDecimal(survey.getAreaDroneFlyingCompleted(), df));
                    survey.setFormateareaSurveySanctionNotStarted(formatBigDecimal(survey.getAreaSurveySanctionNotStarted(), df));
                    return survey;
                })
                .collect(Collectors.toList());

        return surveyList;
    }
    private String formatBigDecimal(Double value, DecimalFormat df) {
        if (value == null || value == 0 || value==0.0) {
            return "0.00";
        }
        return df.format(value);
    }
//    public List<SurveyResurveyViewReport> getSurveyStateDataByStateId(Integer stateId) {
//
//        return surveyResurveyviewRepository.findAllSurveyByStateId(stateId);
//    }


    public List<SurveyResurveyViewReport> getSurveyStateDataByStateId(Integer stateId) {
        DecimalFormat df = new DecimalFormat("#.####");
        List<SurveyResurveyViewReport> surveyList = surveyResurveyviewRepository.findAllSurveyByStateId(stateId);
        return surveyList.stream()
                .map(survey -> {
                    survey.setFormatetotalRuralRevenueArea(formatBigDecimal(survey.getTotalRuralRevenueArea(), df));
                    survey.setFomateareaSanctionedForSurvey(formatBigDecimal(survey.getAreaSanctionedForSurvey(), df));
                    survey.setFormateareaDroneFlyingCompleted(formatBigDecimal(survey.getAreaDroneFlyingCompleted(), df));
                    survey.setFormateareaSurveySanctionNotStarted(formatBigDecimal(survey.getAreaSurveySanctionNotStarted(), df));
                    return survey;
                })
                .collect(Collectors.toList());
    }

    public List<SurveyResurveyViewReport> filterAndSortStateSurveyReport(String parameter, String ascDesc) {
        List<SurveyResurveyViewReport> surveyList = surveyResurveyviewRepository.findAll();
        Comparator<SurveyResurveyViewReport> comparator = Comparator.comparing(SurveyResurveyViewReport::getStateName);
        if ("surveyvillageper".equals(parameter)) {
            comparator = Comparator.comparing(SurveyResurveyViewReport::getVillagesMap1Generated);
        } else if ("ruralArea".equals(parameter)) {
            comparator = Comparator.comparing(SurveyResurveyViewReport::getTotalRuralRevenueArea);
        }
        //New Added
        else if ("AreaSanctionedSurveyResurvey".equals(parameter)) {
            comparator = Comparator.comparing(SurveyResurveyViewReport::getAreaSanctionedForSurvey);
        }
        else if ("DroneflyingcompletedVillages".equals(parameter)) {
            comparator = Comparator.comparing(SurveyResurveyViewReport::getVillagesDroneFlyingCompleted);
        }
        else if ("AreaVillagesDroneflyingcompleted".equals(parameter)) {
            comparator = Comparator.comparing(SurveyResurveyViewReport::getAreaDroneFlyingCompleted);
        }
        else if ("SurveySanctionedbutNotStartedVillages".equals(parameter)) {
            comparator = Comparator.comparing(SurveyResurveyViewReport::getVillagesSurveySanctionNotStarted);
        }

        if ("DESC".equals(ascDesc)) {
            comparator = comparator.reversed();
        }
        return surveyList.stream()
                .filter(survey -> !survey.getStateId().equals(Long.valueOf(999)))
                .map(survey -> {
                    survey.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(survey.getTotalDistrict()));
                    survey.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(survey.getTotalTehsils()));
                    survey.setFormattingTotalVillages(NumberFormatterUtil.formatWithCommas(survey.getTotalVillages()));
                    survey.setFormattingvillagesDroneFlyingCompleted(NumberFormatterUtil.formatWithCommas(survey.getVillagesDroneFlyingCompleted()));
                    survey.setFormattingvillagesMap1Generated(NumberFormatterUtil.formatWithCommas(survey.getVillagesMap1Generated()));
                    survey.setFormattingvillagesDraftMapPublished(NumberFormatterUtil.formatWithCommas(survey.getVillagesDraftMapPublished()));
                    survey.setFormattingvillagesFinalPromulgationDone(NumberFormatterUtil.formatWithCommas(survey.getVillagesFinalPromulgationDone()));
                    survey.setFormattingvillagesSurveySanctionNotStarted(NumberFormatterUtil.formatWithCommas(survey.getVillagesSurveySanctionNotStarted()));
                    survey.setFormatetotalRuralRevenueArea(formatBigDecimal(survey.getTotalRuralRevenueArea(), df));
                    survey.setFomateareaSanctionedForSurvey(formatBigDecimal(survey.getAreaSanctionedForSurvey(), df));
                    survey.setFormateareaDroneFlyingCompleted(formatBigDecimal(survey.getAreaDroneFlyingCompleted(), df));
                    survey.setFormateareaSurveySanctionNotStarted(formatBigDecimal(survey.getAreaSurveySanctionNotStarted(), df));
                    return survey;
                })
                .sorted(comparator)
                .collect(Collectors.toList());
    }



}
