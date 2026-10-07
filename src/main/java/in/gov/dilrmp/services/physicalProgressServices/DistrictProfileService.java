package in.gov.dilrmp.services.physicalProgressServices;

import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.DistrictMapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.DistrictClrReportView;
import in.gov.dilrmp.models.reportDTO.legacy.DistrictLegacyDigitizationReport;
import in.gov.dilrmp.models.reportDTO.legacyRevenue.DistrictLegacyRevenueReport;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.DistrictLinkedAadhaarViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.DistrictMrrViewReport;
import in.gov.dilrmp.models.reportDTO.profile.DistrictProfileItem;
import in.gov.dilrmp.models.reportDTO.profile.DistrictProfileSection;
import in.gov.dilrmp.models.reportDTO.rcms.DistrictRcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.sroModernization.DistrictSroModernizationReport;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.DistrictSurveyResurveyViewReport;
import in.gov.dilrmp.utils.AadhaarReportExportV5Util;
import in.gov.dilrmp.utils.LegacyDigitizationExportUtil;
import in.gov.dilrmp.utils.LegacyRevenueExportUtil;
import in.gov.dilrmp.utils.MapDigitizationReportExportV5Util;
import in.gov.dilrmp.utils.SroModernizationExportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/** Builds the District Profile: every physical-progress component for one district. */
@Service
public class DistrictProfileService {

    private static final Logger logger = LoggerFactory.getLogger(DistrictProfileService.class);

    @Autowired
    DistrictCLRService districtCLRService;
    @Autowired
    DistrictMAPService districtMAPService;
    @Autowired
    DistrictMRRService districtMRRService;
    @Autowired
    DistrictSurveyService districtSurveyService;
    @Autowired
    DistrictRCMSService districtRCMSService;
    @Autowired
    DistrictAadharSService districtAadharSService;
    @Autowired
    LegacyDigitizationReportService legacyDigitizationReportService;
    @Autowired
    SroModernizationReportService sroModernizationReportService;
    @Autowired
    LegacyRevenueReportService legacyRevenueReportService;

    public List<DistrictProfileSection> buildProfile(Long stateId, Long districtId) {
        List<DistrictProfileSection> sections = new ArrayList<>();
        sections.add(section(ReportLabels.CLR_REPORT, () -> clrItems(
                find(districtCLRService.getDistrictReportsByStateId(stateId), DistrictClrReportView::getDistrictId, districtId))));
        sections.add(section(ReportLabels.MAP_DIGITIZATION_REPORT, () -> mapItems(
                find(districtMAPService.getDistrictMAPModelsByStateId(stateId), DistrictMapDigitizationReport::getDistrictId, districtId))));
        sections.add(section(ReportLabels.MRR_REPORT, () -> mrrItems(
                find(districtMRRService.getDistrictReportsByStateId(stateId), DistrictMrrViewReport::getDistrictId, districtId))));
        sections.add(section(ReportLabels.SURVEY_REPORT, () -> surveyItems(
                find(districtSurveyService.getDistrictListByStateId(stateId), DistrictSurveyResurveyViewReport::getDistrictId, districtId))));
        sections.add(section(ReportLabels.RCMS_REPORT, () -> rcmsItems(
                find(districtRCMSService.getDistrictListByStateId(stateId), DistrictRcmsReportDTO::getDistrictId, districtId))));
        sections.add(section(ReportLabels.AADHAR_REPORT, () -> aadhaarItems(
                find(districtAadharSService.getDistrictListByStateId(stateId), DistrictLinkedAadhaarViewReport::getDistrictId, districtId))));
        sections.add(section(ReportLabels.LEGACY_DIGITIZATION_REPORT, () -> legacyItems(
                find(legacyDigitizationReportService.getDistrictListByStateId(stateId), DistrictLegacyDigitizationReport::getDistrictId, districtId))));
        sections.add(section(ReportLabels.SRO_MODERNIZATION_REPORT, () -> sroModernizationItems(
                find(sroModernizationReportService.getDistrictListByStateId(stateId), DistrictSroModernizationReport::getDistrictId, districtId))));
        sections.add(section(ReportLabels.LEGACY_REVENUE_REPORT, () -> legacyRevenueItems(
                find(legacyRevenueReportService.getDistrictListByStateId(stateId), DistrictLegacyRevenueReport::getDistrictId, districtId))));
        return sections;
    }

    private DistrictProfileSection section(String title, Supplier<List<DistrictProfileItem>> items) {
        try {
            return new DistrictProfileSection(title, items.get());
        } catch (Exception e) {
            logger.error("District profile section '{}' could not be loaded", title, e);
            return new DistrictProfileSection(title, new ArrayList<>());
        }
    }

    private static <T> T find(List<T> rows, Function<T, Long> idOf, Long districtId) {
        if (rows == null) {
            return null;
        }
        return rows.stream().filter(r -> Objects.equals(idOf.apply(r), districtId)).findFirst().orElse(null);
    }

    private List<DistrictProfileItem> clrItems(DistrictClrReportView d) {
        List<DistrictProfileItem> items = new ArrayList<>();
        if (d == null) {
            return items;
        }
        add(items, ReportLabels.TOTAL_TEHSILS, num(d.getTotalTehsils()));
        add(items, ReportLabels.TOTAL_VILLAGES, num(d.getTotalVillages()));
        add(items, of(ReportLabels.RoR, ReportLabels.TOTAL), num(d.getTotalRor()));
        add(items, of(ReportLabels.RoR, ReportLabels.COMPUTERIZED), num(d.getRorComputerized()));
        add(items, pctOf(ReportLabels.RoR, ReportLabels.COMPUTERIZED), pct(d.getRorComputerizedPercent()));
        add(items, of(ReportLabels.RoR, ReportLabels.ROR_WITH_CADASTRAL_MAP), num(d.getRorWithCadastralMap()));
        add(items, pctOf(ReportLabels.RoR, ReportLabels.ROR_WITH_CADASTRAL_MAP), pct(d.getRorWithCadastralMapPercent()));
        add(items, noOf(ReportLabels.NUMBER_OF_VILLAGES_WHERE_CLR_COMPLETED), num(d.getVillagesClrCompleted()));
        add(items, pctOf(ReportLabels.NUMBER_OF_VILLAGES_WHERE_CLR_COMPLETED), pct(d.getClrCompletionPercent()));
        add(items, ReportLabels.TOTAL_NO_OF_LAND_OWNERS, num(d.getTotalLandOwners()));
        add(items, ReportLabels.AVAILABILITY_OF_GENDER_BASED_LAND_OWNERSHIP, text(d.getDistrictsWithGenderBasedOwnership(), "0"));
        add(items, of(ReportLabels.AVAILABILITY_OF_GENDER_BASED_LAND_OWNERSHIP, ReportLabels.MALE), num(d.getTotalMaleLandOwners()));
        add(items, of(ReportLabels.AVAILABILITY_OF_GENDER_BASED_LAND_OWNERSHIP, ReportLabels.FEMALE), num(d.getTotalFemaleLandOwners()));
        add(items, of(ReportLabels.AVAILABILITY_OF_GENDER_BASED_LAND_OWNERSHIP, ReportLabels.TOTAL), num(d.getTotalOwners()));
        add(items, yesNo(ReportLabels.WHETHER_ROR_AVAILABLE_ONLINE), flag(d.getRorAvailableOnline()));
        add(items, yesNo(ReportLabels.WHETHER_DIGITALLY_SIGNED_ROR_AVAILABLE_ONLINE), flag(d.getDigitallySignedRorAvailable()));
        add(items, yesNo(ReportLabels.WHETHER_DIGITALLY_SIGNED_ROR_LEGALLY_VALID_IN_STATE), flag(d.getDigitallySignedRorLegallyValid()));
        add(items, yesNo(ReportLabels.WHETHER_MUTATION_APPLICATION_SUBMITTED_ONLINE), flag(d.getOnlineMutationFacility()));
        add(items, yesNo(ReportLabels.WHETHER_AUTO_TRIGGERED_MUTATION_FACILITY_AVAILABLE), flag(d.getAutoTriggerMutation()));
        add(items, yesNo(ReportLabels.WHETHER_AUTO_MUTATION_FACILITY_AVAILABLE), flag(d.getAutoMutationFacility()));
        add(items, yesNo(ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_SRO), flag(d.getLandRecordsOnlineFromRegistrationSystem()));
        add(items, yesNo(ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_REVENUE_COURTS), flag(d.getRevenueCourtProceedingsPaperless()));
        add(items, yesNo(ReportLabels.WHETHER_LAND_RECORDS_BE_CHECKED_ONLINE_BY_CIVIL_COURTS_THROUGH_E_COURTS_SYSTEM),
                flag(d.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts()));
        return items;
    }

    private List<DistrictProfileItem> mapItems(DistrictMapDigitizationReport d) {
        List<DistrictProfileItem> items = new ArrayList<>();
        if (d == null) {
            return items;
        }
        add(items, ReportLabels.TOTAL_TEHSILS, num(d.getTotalTehsils()));
        List<String> cadastral = new ArrayList<>();
        MapDigitizationReportExportV5Util.appendCadastralMapRowV5(cadastral, d, new DecimalFormat("0.00"));
        String[] cadastralLeaf = MapDigitizationReportExportV5Util.cadastralLeafHeaderConstants();
        for (int i = 0; i < cadastralLeaf.length && i < cadastral.size(); i++) {
            add(items, of(ReportLabels.CADASTRAL_MAPS, cadastralLeaf[i]), cadastral.get(i));
        }
        add(items, of(ReportLabels.FMBs, ReportLabels.TOTAL), num(d.getTotalFmbs()));
        add(items, of(ReportLabels.FMBs, ReportLabels.DIGITIZED), num(d.getDigitizedFmbs()));
        add(items, pctOf(ReportLabels.FMBs, ReportLabels.DIGITIZED), pct(d.getDigitizedFmbsPercent()));
        add(items, of(ReportLabels.TIPPANS, ReportLabels.TOTAL), num(d.getTotalTippans()));
        add(items, of(ReportLabels.TIPPANS, ReportLabels.DIGITIZED), num(d.getDigitizedTippans()));
        add(items, pctOf(ReportLabels.TIPPANS, ReportLabels.DIGITIZED), pct(d.getDigitizedTippansPercent()));
        add(items, of(ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS, ReportLabels.TOTAL), num(d.getTotalMapsFmbTippans()));
        add(items, of(ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS, ReportLabels.DIGITIZED), num(d.getTotalDigitizedMapsFmbTippans()));
        add(items, pctOf(ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS, ReportLabels.DIGITIZED), pct(d.getDigitizedMapsFmbTippansPercent()));
        add(items, of(ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS, ReportLabels.GEO_REFERENCED), num(d.getGeoreferencedMaps()));
        add(items, pctOf(ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS, ReportLabels.GEO_REFERENCED), pct(d.getGeoreferencedMapsPercent()));
        add(items, of(ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.TOTAL), num(d.getTotalVillages()));
        add(items, of(ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_LINKED_TO_ROR), num(d.getVillagesLinkedWithRor()));
        add(items, pctOf(ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_LINKED_TO_ROR), pct(d.getVillagesLinkedWithRorPercent()));
        add(items, of(ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_GEO_REFERENCED), num(d.getVillagesGeoreferenced()));
        add(items, pctOf(ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_GEO_REFERENCED), pct(d.getVillagesGeoreferencedPercent()));
        add(items, of(ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.ULPIN_ASSIGNED), num(d.getVillagesWithUlipn()));
        add(items, pctOf(ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.ULPIN_ASSIGNED), pct(d.getVillagesWithUlipnPercent()));
        add(items, of(ReportLabels.NO_OF_LAND_PARCELS, ReportLabels.TOTAL), num(d.getTotalLandParcels()));
        add(items, of(ReportLabels.NO_OF_LAND_PARCELS, ReportLabels.GEO_REFERENCED), num(d.getGeoreferencedLandParcels()));
        add(items, pctOf(ReportLabels.NO_OF_LAND_PARCELS, ReportLabels.GEO_REFERENCED), pct(d.getGeoreferencedLandParcelsPercent()));
        add(items, of(ReportLabels.NO_OF_LAND_PARCELS, ReportLabels.ULPIN_ASSIGNED), num(d.getLandParcelsWithUlipn()));
        add(items, pctOf(ReportLabels.NO_OF_LAND_PARCELS, ReportLabels.ULPIN_ASSIGNED), pct(d.getLandParcelsWithUlipnPercent()));
        add(items, yesNo(ReportLabels.SHOWING_CURRENT_OWNERSHIP), flag(d.getMapsUpdatedBasedOnMutation()));
        add(items, of(ReportLabels.SHOWING_CURRENT_OWNERSHIP, ReportLabels.IF_NO_YEAR_UPTO_WHICH_ARE_UPDATED), text(d.getMapsUpdatePeriodDate(), "-"));
        return items;
    }

    private List<DistrictProfileItem> mrrItems(DistrictMrrViewReport d) {
        List<DistrictProfileItem> items = new ArrayList<>();
        if (d == null) {
            return items;
        }
        add(items, ReportLabels.TOTAL_TEHSILS, num(d.getTotalTehsils()));
        add(items, noOf(ReportLabels.MRR_SANCTIONED), num(d.getMrrSanctioned()));
        add(items, pctOf(ReportLabels.MRR_SANCTIONED), pct(d.getMrrSanctionedPercent()));
        add(items, noOf(ReportLabels.MRR_COMPLETED_OUT_OF_TOTAL), num(d.getMrrCompleted()));
        add(items, pctOf(ReportLabels.MRR_COMPLETED_OUT_OF_TOTAL), pct(d.getMrrCompletedOutOfTotalTehsilsPercent()));
        add(items, noOf(ReportLabels.MRR_COMPLETED_OUT_OF_SANCTIONED), num(d.getMrrCompleted()));
        add(items, pctOf(ReportLabels.MRR_COMPLETED_OUT_OF_SANCTIONED), pct(d.getMrrCompletedOutOfSanctionedPercent()));
        return items;
    }

    private List<DistrictProfileItem> surveyItems(DistrictSurveyResurveyViewReport d) {
        List<DistrictProfileItem> items = new ArrayList<>();
        if (d == null) {
            return items;
        }
        add(items, ReportLabels.TOTAL_TEHSILS, num(d.getTotalTehsils()));
        add(items, ReportLabels.TOTAL_VILLAGES, num(d.getTotalVillages()));
        add(items, ReportLabels.TOTAL_RURAL_REVENUE_AREA_SQ_KM, num(d.getTotalRuralRevenueArea()));
        add(items, ReportLabels.AREA_SANCTIONED_FOR_SURVEY_RE_SURVEY_SQ_KM, num(d.getAreaSanctionedForSurvey()));
        add(items, of(ReportLabels.DRON_FLYING_SURVEY, ReportLabels.NUMBER_OF_VILLAGES), num(d.getVillagesDroneFlyingCompleted()));
        add(items, of(ReportLabels.DRON_FLYING_SURVEY, ReportLabels.AREA_OF_VILLAGES_SQ_KM), num(d.getAreaDroneFlyingCompleted()));
        add(items, of(ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.MAP_1_IS_GENERATED), num(d.getVillagesMap1Generated()));
        add(items, of(ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.DRAFT_MAP_PUBLISHED), num(d.getVillagesDraftMapPublished()));
        add(items, of(ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.FINAL_PROMULGATION_DONE), num(d.getVillagesFinalPromulgationDone()));
        add(items, of(ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.SURVEY_SANCTIONED_NOT_STARTED), num(d.getVillagesSurveySanctionNotStarted()));
        add(items, ReportLabels.AREA_OF_VILLAGES_WHERE_SURVEY_SANCTIONED_NOT_STARTED, num(d.getAreaSurveySanctionNotStarted()));
        return items;
    }

    private List<DistrictProfileItem> rcmsItems(DistrictRcmsReportDTO d) {
        List<DistrictProfileItem> items = new ArrayList<>();
        if (d == null) {
            return items;
        }
        String online = ReportLabels.ONLINE_PROCESS_OF_REVENUE_COURT_PROCESSES_AVAILABLE;
        add(items, ReportLabels.TOTAL_TEHSILS, num(d.getTotalTehsils()));
        add(items, of(ReportLabels.NUMBER_OF_REVENUE_COURTS, ReportLabels.TOTAL), num(d.getTotalRevenueCourts()));
        add(items, of(ReportLabels.NUMBER_OF_REVENUE_COURTS, ReportLabels.COMPUTERIZED_ONLINE), num(d.getRevenueCourtsComputerized()));
        add(items, pctOf(ReportLabels.NUMBER_OF_REVENUE_COURTS, ReportLabels.COMPUTERIZED_ONLINE), pct(d.getRevenueCourtsComputerizedPercent()));
        add(items, yesNo(ReportLabels.E_REVENUE_COURT_MANAGEMENT_SYSTEM), flag(d.getERcmsAvailable()));
        add(items, yesNo(of(online, ReportLabels.ATTACHED_DOCUMENTS)), flag(d.getAffidavitFilingOnline()));
        add(items, yesNo(of(online, ReportLabels.CAUSE_LIST_GENERATION)), flag(d.getCauseListGenerationOnline()));
        add(items, yesNo(of(online, ReportLabels.DEFENDANT_NOTIFICATION)), flag(d.getNoticeToDefendantsOnline()));
        add(items, yesNo(of(online, ReportLabels.COUNTER_AFFIDAVIT_FILING)), flag(d.getCounterAffidavitFilingOnline()));
        add(items, yesNo(of(online, ReportLabels.PROCEEDINGS_TYPING_SYSTEM)), flag(d.getProceedingsTypingDirectlyOnline()));
        add(items, yesNo(of(online, ReportLabels.UPLOADING_COURT_ORDERS_NOTIFICATION)), flag(d.getUploadingRevenueCourtOrdersOnline()));
        add(items, yesNo(of(online, ReportLabels.LAND_RECORD_INTEGRATION)), flag(d.getLandRecordsOnlineFromRevenueCourtSystem()));
        add(items, yesNo(of(online, ReportLabels.ONLINE_LAND_RECORD_CHECK)), flag(d.getRevenueCourtProceedingsPaperless()));
        add(items, yesNo(ReportLabels.WHETHER_PROCEEDS), flag(d.getLandRecordsOnlineForCivilCourts()));
        add(items, yesNo(ReportLabels.WHETHER_LAW_RECORD_ONLINE_COURT), flag(d.getCaseFilingRedFlaggedInLandRecordsFromCivilCourts()));
        add(items, yesNo(ReportLabels.WHETHER_FILING_DIRECTLY_SYSTEM), flag(d.getCaseFilingRedFlaggedInLandRecords()));
        return items;
    }

    private List<DistrictProfileItem> aadhaarItems(DistrictLinkedAadhaarViewReport d) {
        List<DistrictProfileItem> items = new ArrayList<>();
        if (d == null) {
            return items;
        }
        add(items, ReportLabels.TOTAL_TEHSILS, num(d.getTotalTehsils()));
        add(items, of(ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.TOTAL), num(d.getTotalVillages()));
        add(items, of(ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.WHERE_AT_LEAST_ONE_RoR_LINKED_WITH_AADHAAR), num(d.getVillagesWithRorLinkedAadhaar()));
        add(items, pctOf(ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.WHERE_AT_LEAST_ONE_RoR_LINKED_WITH_AADHAAR), pct(d.getVillagesWithRorLinkedAadhaarPercent()));
        add(items, of(ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.WHERE_100_PERCENT_RoR_LINKED_WITH_AADHAAR), num(d.getVillagesWith100PercentRorLinkedAadhaar()));
        add(items, pctOf(ReportLabels.NUMBER_OF_VILLAGES, ReportLabels.WHERE_100_PERCENT_RoR_LINKED_WITH_AADHAAR), pct(d.getVillagesWith100PercentRorLinkedAadhaarPercent()));
        add(items, of(ReportLabels.RoR, ReportLabels.TOTAL), num(d.getTotalRor()));
        add(items, of(ReportLabels.RoR, ReportLabels.LINKED_WITH_AADHAAR), num(d.getRorLinkedWithAadhaar()));
        add(items, pctOf(ReportLabels.RoR, ReportLabels.LINKED_WITH_AADHAAR), pct(d.getRorLinkedWithAadhaarPercent()));
        add(items, of(ReportLabels.RoR, ReportLabels.LINKED_WITH_MOBILE_NUMBER), num(d.getRorLinkedWithMobileNumber()));
        add(items, pctOf(ReportLabels.RoR, ReportLabels.LINKED_WITH_MOBILE_NUMBER), pct(d.getRorLinkedWithMobileNumberPercent()));
        List<String> address = new ArrayList<>();
        AadhaarReportExportV5Util.appendAddressRowDistrict(address, d);
        zip(items, address,
                of(ReportLabels.RoR, ReportLabels.LINKED_WITH_ADDRESS),
                pctOf(ReportLabels.RoR, ReportLabels.LINKED_WITH_ADDRESS));
        List<String> owners = new ArrayList<>();
        AadhaarReportExportV5Util.appendLandOwnerRowDistrict(owners, d);
        zip(items, owners,
                of(ReportLabels.NUMBER_OF_LAND_OWNERS, ReportLabels.TOTAL),
                of(ReportLabels.NUMBER_OF_LAND_OWNERS, ReportLabels.LINKED_WITH_AADHAAR),
                pctOf(ReportLabels.NUMBER_OF_LAND_OWNERS, ReportLabels.LINKED_WITH_AADHAAR),
                of(ReportLabels.NUMBER_OF_LAND_OWNERS, ReportLabels.LINKED_WITH_MOBILE_NUMBER),
                pctOf(ReportLabels.NUMBER_OF_LAND_OWNERS, ReportLabels.LINKED_WITH_MOBILE_NUMBER),
                of(ReportLabels.NUMBER_OF_LAND_OWNERS, ReportLabels.LINKED_WITH_ADDRESS),
                pctOf(ReportLabels.NUMBER_OF_LAND_OWNERS, ReportLabels.LINKED_WITH_ADDRESS));
        return items;
    }

    private List<DistrictProfileItem> legacyItems(DistrictLegacyDigitizationReport d) {
        List<DistrictProfileItem> items = new ArrayList<>();
        if (d == null) {
            return items;
        }
        add(items, ReportLabels.TOTAL_TEHSILS, num(d.getTotalTehsils()));
        List<String> values = new ArrayList<>();
        LegacyDigitizationExportUtil.appendDistrict(values, d);
        zip(items, values,
                pages(ReportLabels.TOTAL_LEGACY_REGISTERED_DOCUMENTS),
                pages(ReportLabels.LEGACY_DIGITIZED_FROM_STATE_FUNDS),
                pctOf(ReportLabels.LEGACY_DIGITIZED_FROM_STATE_FUNDS),
                pages(ReportLabels.LEGACY_SANCTIONED_UNDER_DILRMP),
                pages(ReportLabels.LEGACY_COMPLETED_FROM_DILRMP_FUNDS),
                pctOf(ReportLabels.LEGACY_COMPLETED_FROM_DILRMP_FUNDS),
                pages(ReportLabels.TOTAL_LEGACY_DIGITIZED),
                pctOf(ReportLabels.TOTAL_LEGACY_DIGITIZED),
                ReportLabels.LEGACY_DIGITIZED_UPTO_YEAR);
        return items;
    }

    private List<DistrictProfileItem> sroModernizationItems(DistrictSroModernizationReport d) {
        List<DistrictProfileItem> items = new ArrayList<>();
        if (d == null) {
            return items;
        }
        add(items, ReportLabels.TOTAL_TEHSILS, num(d.getTotalTehsils()));
        add(items, ReportLabels.TOTAL_SROS, num(d.getTotalSros()));
        List<String> values = new ArrayList<>();
        SroModernizationExportUtil.appendDistrict(values, d);
        zip(items, values,
                ReportLabels.SROS_USING_ONLINE_REGISTRATION_A,
                noOf(ReportLabels.SROS_MODERNISED_STATE_FUNDS),
                pctOf(ReportLabels.SROS_MODERNISED_STATE_FUNDS),
                ReportLabels.SROS_SANCTIONED_DILRMP,
                noOf(ReportLabels.SROS_MODERNISED_DILRMP_FUNDS),
                pctOf(ReportLabels.SROS_MODERNISED_DILRMP_FUNDS),
                noOf(ReportLabels.SROS_MODERNISED_TOTAL),
                pctOf(ReportLabels.SROS_MODERNISED_TOTAL));
        return items;
    }

    private List<DistrictProfileItem> legacyRevenueItems(DistrictLegacyRevenueReport d) {
        List<DistrictProfileItem> items = new ArrayList<>();
        if (d == null) {
            return items;
        }
        add(items, ReportLabels.TOTAL_TEHSILS, num(d.getTotalTehsils()));
        List<String> values = new ArrayList<>();
        LegacyRevenueExportUtil.appendDistrict(values, d);
        zip(items, values,
                pages(ReportLabels.TOTAL_LEGACY_REVENUE_RECORDS),
                pages(ReportLabels.LEGACY_REVENUE_DIGITISED_STATE_FUNDS),
                pctOf(ReportLabels.LEGACY_REVENUE_DIGITISED_STATE_FUNDS),
                pages(ReportLabels.LEGACY_REVENUE_SANCTIONED_DILRMP),
                pages(ReportLabels.LEGACY_REVENUE_COMPLETED_DILRMP),
                pctOf(ReportLabels.LEGACY_REVENUE_COMPLETED_DILRMP),
                pages(ReportLabels.TOTAL_LEGACY_REVENUE_DIGITISED),
                pctOf(ReportLabels.TOTAL_LEGACY_REVENUE_DIGITISED),
                ReportLabels.LEGACY_REVENUE_UPTO_YEAR);
        return items;
    }

    private static void add(List<DistrictProfileItem> items, String label, String value) {
        items.add(new DistrictProfileItem(label, value));
    }

    private static void zip(List<DistrictProfileItem> items, List<String> values, String... labels) {
        for (int i = 0; i < labels.length && i < values.size(); i++) {
            add(items, labels[i], values.get(i));
        }
    }

    private static String of(String group, String leaf) {
        return group + " - " + leaf;
    }

    private static String noOf(String label) {
        return label + " (" + ReportLabels.NO + ")";
    }

    private static String pctOf(String label) {
        return label + " (" + ReportLabels.PERCENTAGE + ")";
    }

    private static String pctOf(String group, String leaf) {
        return pctOf(of(group, leaf));
    }

    private static String pages(String label) {
        return label + " (" + ReportLabels.NO_OF_PAGES + ")";
    }

    private static String yesNo(String label) {
        return label + " (" + ReportLabels.YES_NO + ")";
    }

    private static String num(Object value) {
        return value == null ? "0" : String.valueOf(value);
    }

    private static String pct(Number value) {
        return value == null ? "0.00" : new DecimalFormat("0.00").format(value);
    }

    private static String flag(String value) {
        return value == null || value.isBlank() ? "NO" : value;
    }

    private static String text(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
