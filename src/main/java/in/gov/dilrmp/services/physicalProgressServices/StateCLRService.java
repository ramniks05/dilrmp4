package in.gov.dilrmp.services.physicalProgressServices;
import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.repositories.physicalProgressRepositories.StateClrReportViewRepository;
import in.gov.dilrmp.utils.ClrReportV5Enricher;
import in.gov.dilrmp.utils.NumberFormatterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StateCLRService {

    @Autowired
    StateClrReportViewRepository stateClrReportViewRepository;
    @Autowired
    ClrReportV5Enricher clrReportV5Enricher;


    private Logger logger = LoggerFactory.getLogger(StateCLRService.class);


    public Map<String, String> createLabels() {
        Map<String, String> labels = new HashMap<>();
        labels.put("serialNumber", ReportLabels.SERIAL_NUMBER);
        labels.put("stateUT", ReportLabels.STATE_UT);
        labels.put("totalState", ReportLabels.TOTAL_STATE_UT);
        labels.put("totalDistricts", ReportLabels.TOTAL_DISTRICTS);
        labels.put("totalTehsils", ReportLabels.TOTAL_TEHSILS);
        labels.put("districtName", ReportLabels.DISTRICT_NAME);
        labels.put("totalVillages", ReportLabels.TOTAL_VILLAGES);
        labels.put("totalRors", ReportLabels.TOTAL_RORS);
        labels.put("numberOfLandOwners", ReportLabels.NUMBER_OF_LAND_OWNERS);
        labels.put("male", ReportLabels.MALE);
        labels.put("female", ReportLabels.FEMALE);
        labels.put("total", ReportLabels.TOTAL);
        labels.put("RoR", ReportLabels.RoR);
        labels.put("rorComputerized", ReportLabels.ROR_COMPUTERIZED);
        labels.put("computerized", ReportLabels.COMPUTERIZED);
        labels.put("rorWithCadastralMap", ReportLabels.ROR_WITH_CADASTRAL_MAP); //v5
        labels.put("clrCompleted", ReportLabels.CLR_COMPLETED);
        labels.put("numberOfVillagesWhererCLRCompleted", ReportLabels.NUMBER_OF_VILLAGES_WHERE_CLR_COMPLETED);
        labels.put("numberOfVillages", ReportLabels.NUMBER_OF_VILLAGES);
        labels.put("digitallySignedRorAvailable", ReportLabels.DIGITALLY_SIGNED_ROR_AVAILABLE);
        labels.put("rorAccessibleCscKioskOnline", ReportLabels.ROR_ACCESSIBLE_CSC_KIOSK_ONLINE);
        labels.put("onlineMutationFacility", ReportLabels.ONLINE_MUTATION_FACILITY);
        labels.put("autoTriggeredMutationFacility", ReportLabels.AUTO_TRIGGERED_MUTATION_FACILITY);
        labels.put("rorIntegratedWithBanks", ReportLabels.ROR_INTEGRATED_WITH_BANKS);
        labels.put("tehsilsLinkedWithCivilCourts", ReportLabels.TEHSILS_LINKED_WITH_CIVIL_COURTS);
        labels.put("number", ReportLabels.NO);
        labels.put("percentage", ReportLabels.PERCENTAGE);
        labels.put("reportName", ReportLabels.CLR_REPORT);
        labels.put("grandTotal", ReportLabels.GRAND_TOTAL);
        //Newly Added Columns :
        labels.put("numberOfVillagesWhereCLRCompleted", ReportLabels.NUMBER_OF_VILLAGES_WHERE_CLR_COMPLETED);
        labels.put("totalNoOfLandOwners", ReportLabels.TOTAL_NO_OF_LAND_OWNERS);
        labels.put("availabilityOfGenderBasedLandOwnership", ReportLabels.AVAILABILITY_OF_GENDER_BASED_LAND_OWNERSHIP);
        labels.put("numberOfDistrictsWhereAvailable", ReportLabels.NUMBER_OF_DISTRICTS_WHERE_AVAILABLE);
        labels.put("numberOfStateWhereAvailable", ReportLabels.NUMBER_OF_STATE_WHERE);
        labels.put("whetherRoRAvailableOnline", ReportLabels.WHETHER_ROR_AVAILABLE_ONLINE);
        labels.put("RoRAvailableOnline", ReportLabels.ROR_AVAILABLE_ONLINE);
        labels.put("whetherDigitallySignedRoRAvailableOnline", ReportLabels.WHETHER_DIGITALLY_SIGNED_ROR_AVAILABLE_ONLINE);
        labels.put("DigitallySignedRoRAvailableOnline", ReportLabels.DIGITALLY_SIGNED_ROR_AVAILABLE_ONLINE);
        labels.put("whetherDigitallySignedRoRLegallyValid", ReportLabels.WHETHER_DIGITALLY_SIGNED_ROR_LEGALLY_VALID_IN_STATE);
        labels.put("DigitallySignedRoRLegallyValid", ReportLabels.DIGITALLY_SIGNED_ROR_LEGALLY_VALID_IN_STATE);
        labels.put("whetherLandRecordsCheckedOnlineBySRO", ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_SRO);
        labels.put("LandRecordsCheckedOnlineBySRO", ReportLabels.LAND_RECORDS_BE_CHECKED_ONLINE_BY_SRO);
        labels.put("whetherMutationApplicationSubmittedOnline", ReportLabels.WHETHER_MUTATION_APPLICATION_SUBMITTED_ONLINE);
        labels.put("MutationApplicationSubmittedOnline", ReportLabels.MUTATION_APPLICATION_SUBMITTED_ONLINE);
        labels.put("whetherAutoTriggeredMutationFacilityAvailable", ReportLabels.WHETHER_AUTO_TRIGGERED_MUTATION_FACILITY_AVAILABLE);
        labels.put("AutoTriggeredMutationFacilityAvailable", ReportLabels.AUTO_TRIGGERED_MUTATION_FACILITY_AVAILABLE);
        labels.put("whetherAutoMutationFacilityAvailable", ReportLabels.WHETHER_AUTO_MUTATION_FACILITY_AVAILABLE); //v5
        labels.put("AutoMutationFacilityAvailable", ReportLabels.AUTO_MUTATION_FACILITY_AVAILABLE); //v5
        labels.put("whetherBanksAuthorizedToCreateClearMortgageChargeInRoR", ReportLabels.WHETHER_BANKS_AUTHORIZED_TO_CREATE_CLEAR_MORTGAGE_CHARGE_IN_ROR);
        labels.put("BanksAuthorizedToCreateClearMortgageChargeInRoR", ReportLabels.BANKS_AUTHORIZED_TO_CREATE_CLEAR_MORTGAGE_CHARGE_IN_ROR);
        labels.put("whetherLandRecordsCheckedOnlineByRevenueCourts", ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_REVENUE_COURTS);
        labels.put("LandRecordsCheckedOnlineByRevenueCourts", ReportLabels.LAND_RECORDS_BE_CHECKED_ONLINE_BY_REVENUE_COURTS);
        labels.put("whetherLandRecordsCheckedOnlineByCivilECourtsSystem", ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_CIVIL_COURTS_THROUGH_E_COURTS_SYSTEM);
        labels.put("LandRecordsCheckedOnlineByCivilECourtsSystem", ReportLabels.LAND_RECORDS_BE_CHECKED_ONLINE_BY_CIVIL_COURTS_THROUGH_E_COURTS_SYSTEM);
        labels.put("yesNo", ReportLabels.YES_NO);
        labels.put("numberOfDistrictAuthorized", ReportLabels.NUMBER_OF_DISTRICTS_AUTHORIZED);
        labels.put("numberOfBankBranchAuthorized", ReportLabels.NUMBER_OF_BANK_BRANCHES_AUTHORIZED);
        labels.put("genderWonership", ReportLabels.GENDER_OWNERSHIP_HEADING);


        return labels;
    }


    public List<StateClrReportView> getClrStateViwe(){
        List<StateClrReportView> clrList=stateClrReportViewRepository.findAll(Sort.by(Sort.Direction.ASC, "stateName"));
        clrList = clrList.stream()
                .filter(clr -> !clr.getLgdCode().equals(999))
                .map(clr -> {
                    clr.setFormattingTotalRor(NumberFormatterUtil.formatWithCommas(clr.getTotalRor()));
                    clr.setFormattingvillagesClrCompleted(NumberFormatterUtil.formatWithCommas(clr.getVillagesClrCompleted()));
                    clr.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(clr.getTotalDistrict()));
                    clr.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(clr.getTotalTehsils()));
                    clr.setFormattingTotalVillages(NumberFormatterUtil.formatWithCommas(clr.getTotalVillages()));
                    clr.setFormattingTotalLandOwners(NumberFormatterUtil.formatWithCommas(clr.getTotalLandOwners()));
                    clr.setFormattingDistrictsWithBankRedFlagMortgage(NumberFormatterUtil.formatWithCommas(clr.getDistrictsWithBankRedFlagMortgage().intValue()));
                    clr.setFormattingBankBranchesWithRedFlagMortgage(NumberFormatterUtil.formatWithCommas(clr.getBankBranchesWithRedFlagMortgage()));
                    clr.setFormattingRorComputerized(NumberFormatterUtil.formatWithCommas(clr.getRorComputerized()));
                    clr.setFormattingTotalMaleLandOwners(NumberFormatterUtil.formatWithCommas(clr.getTotalMaleLandOwners()));
                    clr.setFormattingTotalFemaleLandOwners(NumberFormatterUtil.formatWithCommas(clr.getTotalFemaleLandOwners()));
                    clr.setFormattingTotalOwners(NumberFormatterUtil.formatWithCommas(clr.getTotalOwners()));
                    return clr;
                })
                .collect(Collectors.toList());
        clrReportV5Enricher.enrichStateReports(clrList);
        return clrList;
    }


    public List<StateClrReportView> getStateClrReportsGrandToatal() {
        List<StateClrReportView> clrList=stateClrReportViewRepository.findAllByStateId(999);
        clrList = clrList.stream()
                .map(clr -> {
                    clr.setFormattingTotalRor(NumberFormatterUtil.formatWithCommas(clr.getTotalRor()));
                    clr.setFormattingvillagesClrCompleted(NumberFormatterUtil.formatWithCommas(clr.getVillagesClrCompleted()));
                    clr.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(clr.getTotalDistrict()));
                    clr.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(clr.getTotalTehsils()));
                    clr.setFormattingTotalVillages(NumberFormatterUtil.formatWithCommas(clr.getTotalVillages()));
                    clr.setFormattingTotalLandOwners(NumberFormatterUtil.formatWithCommas(clr.getTotalLandOwners()));
                    clr.setFormattingDistrictsWithBankRedFlagMortgage(NumberFormatterUtil.formatWithCommas(clr.getDistrictsWithBankRedFlagMortgage().intValue()));
                    clr.setFormattingBankBranchesWithRedFlagMortgage(NumberFormatterUtil.formatWithCommas(clr.getBankBranchesWithRedFlagMortgage()));
                    clr.setFormattingRorComputerized(NumberFormatterUtil.formatWithCommas(clr.getRorComputerized()));
                    clr.setFormattingTotalMaleLandOwners(NumberFormatterUtil.formatWithCommas(clr.getTotalMaleLandOwners()));
                    clr.setFormattingTotalFemaleLandOwners(NumberFormatterUtil.formatWithCommas(clr.getTotalFemaleLandOwners()));
                    clr.setFormattingTotalOwners(NumberFormatterUtil.formatWithCommas(clr.getTotalOwners()));
                    return clr;
                })
                .collect(Collectors.toList());
        clrReportV5Enricher.enrichStateReports(clrList);
        return clrList;
    }

    public List<StateClrReportView> filterAndSortStateClrReport(String parameter, String ascDesc) {
        List<StateClrReportView> stateClrList = stateClrReportViewRepository.findAll();
        Comparator<StateClrReportView> comparator = Comparator.comparing(StateClrReportView::getStateName);
        if ("Computerized".equals(parameter)) {
            comparator = Comparator.comparing(StateClrReportView::getRorComputerizedPercent);
        } else if ("clrCompleted".equals(parameter)) {
            comparator = Comparator.comparing(StateClrReportView::getClrCompletionPercent);
        }
        else if ("genLOwnershipDist".equals(parameter)) {
            comparator = Comparator.comparing(StateClrReportView::getDistrictsWithGenderBasedOwnership);
        }
        else if ("genLOwnershipTotal".equals(parameter)) {
            comparator = Comparator.comparing(StateClrReportView::getTotalOwners);
        }
        if ("DESC".equals(ascDesc)) {
            comparator = comparator.reversed();
        }
        List<StateClrReportView> sorted = stateClrList.stream()
                .filter(clr -> !clr.getLgdCode().equals(999))
                .map(clr -> {
                    clr.setFormattingTotalRor(NumberFormatterUtil.formatWithCommas(clr.getTotalRor()));
                    clr.setFormattingvillagesClrCompleted(NumberFormatterUtil.formatWithCommas(clr.getVillagesClrCompleted()));
                    clr.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(clr.getTotalDistrict()));
                    clr.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(clr.getTotalTehsils()));
                    clr.setFormattingTotalVillages(NumberFormatterUtil.formatWithCommas(clr.getTotalVillages()));
                    clr.setFormattingTotalLandOwners(NumberFormatterUtil.formatWithCommas(clr.getTotalLandOwners()));
                    clr.setFormattingDistrictsWithBankRedFlagMortgage(NumberFormatterUtil.formatWithCommas(clr.getDistrictsWithBankRedFlagMortgage().intValue()));
                    clr.setFormattingBankBranchesWithRedFlagMortgage(NumberFormatterUtil.formatWithCommas(clr.getBankBranchesWithRedFlagMortgage()));
                    clr.setFormattingRorComputerized(NumberFormatterUtil.formatWithCommas(clr.getRorComputerized()));
                    clr.setFormattingTotalMaleLandOwners(NumberFormatterUtil.formatWithCommas(clr.getTotalMaleLandOwners()));
                    clr.setFormattingTotalFemaleLandOwners(NumberFormatterUtil.formatWithCommas(clr.getTotalFemaleLandOwners()));
                    clr.setFormattingTotalOwners(NumberFormatterUtil.formatWithCommas(clr.getTotalOwners()));
                    return clr;
                })
                .sorted(comparator)
                .collect(Collectors.toList());
        clrReportV5Enricher.enrichStateReports(sorted);
        return sorted;
    }

    public List<StateClrReportView> getClrStateDataByStateId(int stateId) {
        List<StateClrReportView> reports = stateClrReportViewRepository.findAllByStateId(stateId);
        clrReportV5Enricher.enrichStateReports(reports);
        return reports;
    }
}
