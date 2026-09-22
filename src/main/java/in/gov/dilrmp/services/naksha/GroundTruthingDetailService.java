package in.gov.dilrmp.services.naksha;

import in.gov.dilrmp.models.naksha.GroundTruthingDetail;
import in.gov.dilrmp.models.naksha.IECActivities;
import in.gov.dilrmp.models.naksha.NakshaMISDataEntry;
import in.gov.dilrmp.models.naksha.ULBMaster;
import in.gov.dilrmp.repositories.naksha.CapacityBuildingRepository;
import in.gov.dilrmp.repositories.naksha.GroundTruthingDetailRepository;
import in.gov.dilrmp.repositories.naksha.ULBMsterRepository;
import in.gov.dilrmp.utils.DropdownOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GroundTruthingDetailService {

    @Autowired
    private GroundTruthingDetailRepository truthingDetailRepository;

    @Autowired
    ULBMsterRepository ulbMsterRepository;


    private List<GroundTruthingDetail> vendor;

    public void loadGroundTruthingDetailData() {
        vendor = truthingDetailRepository.findAll();
    }

    public List<GroundTruthingDetail> getAllEntries() {
        return vendor;
    }

    private static final Logger logger = LoggerFactory.getLogger(GroundTruthingDetailService.class);

    public List<GroundTruthingDetail> findAll() {
        try {
            return truthingDetailRepository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching IEC activities", e);
            throw e; // rethrow or wrap in custom exception if needed
        }
    }


    public List<GroundTruthingDetail> findByUlbMuserId(Long ulbMasterID) {
        return truthingDetailRepository.findByUlbMuserId(ulbMasterID);
    }



    public GroundTruthingDetail save(GroundTruthingDetail groundTruthingDetail) {
        return truthingDetailRepository.save(groundTruthingDetail);
    }

    public LocalDate getUpdateOnDateByUserId(Long userId) {
        return truthingDetailRepository.findUpdateOnDateByUserId(userId);
    }


    public List<Map<String, Object>> getGroundTruthingReportData() {
        if (vendor == null) {
            loadGroundTruthingDetailData();
        }

        List<Map<String, Object>> reportList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy"); // e.g. 2 July 2025

        for (GroundTruthingDetail detail : vendor) {
            Map<String, Object> row = new LinkedHashMap<>();

            /*row.put("stateName", detail.getState() != null ? detail.getState().getName() : "");*/
            row.put("stateName",
                    detail.getState() != null
                            ? formatStateName(detail.getState().getName())
                            : ""
            );
            row.put("ulbName", detail.getUlbMaster() != null ? detail.getUlbMaster().getUlb_name() : "");

            row.put("groundTruthingCommencementDate",
                    formatDate(detail.getGroundTruthingCommencementDate(), formatter));
            row.put("groundTruthingCompletionDate",
                    formatDate(detail.getGroundTruthingCompletionDate(), formatter));

            row.put("fieldTeamsSanctioned", detail.getUlbMaster().getNoOfTeams());
            row.put("fieldSurveyTeamsFormed", detail.getUlbWiseFieldSurveyTeamsFormed());


            row.put("surveyUnitName",
                    detail.getSurveyUnitNameOptions() != null
                            ? DropdownOptions.SurveyUnitType.fromCode(detail.getSurveyUnitNameOptions()).getDescription()
                            : ""
            );




           /* row.put("surveyType",
                    detail.getTypeOfSurveyDone() != null
                            ? DropdownOptions.SurveyType.fromCode(Integer.parseInt(String.valueOf(detail.getTypeOfSurveyDone()))).getDescription()
                            : ""
            );*/


            /* row.put("surveyUnitName", detail.getSurveyUnitText());*/
           /* row.put("surveyType", detail.getTypeOfSurveyDone());*/

           /* row.put("totalAreaToBeSurveyed", detail.getUlbMaster().getFieldSurveyArea());*/
            String surveyType = detail.getTypeOfSurveyDone() != null
                    ? DropdownOptions.SurveyType
                    .fromCode(Integer.parseInt(String.valueOf(detail.getTypeOfSurveyDone())))
                    .getDescription()
                    : "";

            row.put("surveyType", surveyType);

// conditionally set totalAreaToBeSurveyed
            if ("Area".equalsIgnoreCase(surveyType)) {
                row.put("totalAreaToBeSurveyed", detail.getUlbMaster().getFieldSurveyArea());
            } else {
                row.put("totalAreaToBeSurveyed", "");
            }

            row.put("areaSurveyedTillDate", detail.getAreaFieldSurveyCompletedULB());
            row.put("areaWiseSurveyCompletionPercent", detail.getAreaWiseSurveyCompletionPercent());
            row.put("totalPropertiesForFieldSurvey", detail.getTotalPlotsToSurvey());
            row.put("propertiesSurveyedTillDate", detail.getPlotsSurveyedCompleted());
            row.put("propertyWiseSurveyCompletionPercent", detail.getSurveyCompletionPercent());
            row.put("totalSurveyUnits", detail.getTotalSurveyUnits());
            row.put("surveyUnitsSurveyed", detail.getSurveyUnitsSurveyed());
            row.put("surveyUnitsPending", detail.getSurveyUnitsPending());
            row.put("surveyUnitWiseCompletionPercent", detail.getSurveyUnitCompletionPercent());

            row.put("workValidationStatus",
                    detail.getWorkValidatedBySupervisoryOfficer() != null
                            ? DropdownOptions.WorkValidationStatus.fromCode(detail.getWorkValidatedBySupervisoryOfficer()).getDescription()
                            : ""
            );


            /*  row.put("workValidationStatus", detail.getWorkValidatedBySupervisoryOfficer());*/
            row.put("draftUrProCardIssues", detail.getTotalUrProCardIssued());
            row.put("claimsAndObjectionsReceived", detail.getClaimsObjectionsReceived());
            row.put("claimsAndObjectionsResolved", detail.getClaimsObjectionsResolved());
            row.put("claimsResolutionPercent", detail.getClaimsResolutionPercentage());
            row.put("claimsPending", detail.getNoOfClaimsObjectionsPending());
            row.put("claimsPendingPercent", detail.getPercentOfClaimsDisputesResolution());
            row.put("finalUrProCardIssued", detail.getTotalFinalUrProCardIssued());

            reportList.add(row);
        }

        // ✅ Sort by state name ascending
        reportList.sort(Comparator.comparing(row -> ((String) row.get("stateName"))));

        return reportList;
    }

    private String formatDate(LocalDate date, DateTimeFormatter formatter) {
        return (date != null) ? date.format(formatter) : "";
    }

    private String formatStateName(String stateName) {
        if (stateName == null || stateName.trim().isEmpty()) {
            return "";
        }

        // Remove (UT)
        stateName = stateName.replace("(UT)", "").trim();

        // Convert to camel case (first letter capital, rest small)
        String[] words = stateName.toLowerCase().split("\\s+");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return formatted.toString().trim();
    }

}
