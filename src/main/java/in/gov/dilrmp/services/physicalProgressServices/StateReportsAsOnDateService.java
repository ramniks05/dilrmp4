package in.gov.dilrmp.services.physicalProgressServices;

import in.gov.dilrmp.Scheduler.repositories.DistrictMISDataEntryBackupRepository;
import in.gov.dilrmp.Scheduler.repositories.LinkedAadhaarViewReportBackupRepository;
import in.gov.dilrmp.Scheduler.repositories.MapDigitizationReportBackupRepository;
import in.gov.dilrmp.Scheduler.repositories.MrrViewReportBackupRepository;
import in.gov.dilrmp.Scheduler.repositories.RemsReportViewReportBackupRepository;
import in.gov.dilrmp.Scheduler.repositories.StateRegistrationSystemBackupRepository;
import in.gov.dilrmp.Scheduler.repositories.StateSroViewReportBackupRepository;
import in.gov.dilrmp.Scheduler.repositories.SurveyResurveyViewReportBackupRepository;
import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.legacy.LegacyDigitizationReport;
import in.gov.dilrmp.models.reportDTO.legacyRevenue.LegacyRevenueReport;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.models.reportDTO.sroModernization.SroModernizationReport;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.SurveyResurveyViewReport;
import in.gov.dilrmp.utils.LegacyDigitizationReportBuilder;
import in.gov.dilrmp.utils.LegacyRevenueReportBuilder;
import in.gov.dilrmp.utils.SroModernizationReportBuilder;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.IgrMISDataEntryRepository;
import in.gov.dilrmp.utils.AsOnDateEntryResolver;
import in.gov.dilrmp.utils.NumberFormatterUtil;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Builds the state-level physical progress reports as on a past date from the nightly *_backup snapshots
 * (latest snapshot on or before the date). For today or later the live report data is returned.
 * Rows are the same report classes the live screens and PDF exports use.
 */
@Service
public class StateReportsAsOnDateService {

    private static final long GRAND_TOTAL_ID = 999L;

    public enum Component {
        CLR(ReportLabels.CLR_REPORT, "stateClrList", "clrGrandTotal"),
        AADHAAR(ReportLabels.AADHAR_REPORT, "linkedAadharViewReportList", "linkedAadharGrandTotal"),
        MAP(ReportLabels.MAP_DIGITIZATION_REPORT, "reportData", "mapGrandTotal"),
        MRR(ReportLabels.MRR_REPORT, "mrrViewReportList", "mrrGrandTotal"),
        SURVEY(ReportLabels.SURVEY_REPORT, "surveyResurveyViewReportList", "surveyResurveyGrandTotal"),
        RCMS(ReportLabels.RCMS_REPORT, "rcmsReportList", "rcmsGrandTotal"),
        SRO(ReportLabels.SRO_REPORT, "sroStateList", "sroGrandTotal"),
        LEGACY(ReportLabels.LEGACY_DIGITIZATION_REPORT, "legacyList", "legacyGrandTotal"),
        SRO_MODERNIZATION(ReportLabels.SRO_MODERNIZATION_REPORT, "sroModernizationList", "sroModernizationGrandTotal"),
        LEGACY_REVENUE(ReportLabels.LEGACY_REVENUE_REPORT, "legacyRevenueList", "legacyRevenueGrandTotal");

        @Getter
        private final String label;
        /** Session keys read by PhyscialProgressReportControllerPdf. */
        @Getter
        private final String listSessionKey;
        @Getter
        private final String grandTotalSessionKey;

        Component(String label, String listSessionKey, String grandTotalSessionKey) {
            this.label = label;
            this.listSessionKey = listSessionKey;
            this.grandTotalSessionKey = grandTotalSessionKey;
        }
    }

    @Getter
    public static class Snapshot<T> {
        private final List<T> stateList;
        private final List<T> grandTotal;
        private final LocalDate snapshotDate;
        private final boolean live;
        private final List<String> approximateStates;

        Snapshot(List<T> stateList, List<T> grandTotal, LocalDate snapshotDate, boolean live, List<String> approximateStates) {
            this.stateList = stateList;
            this.grandTotal = grandTotal;
            this.snapshotDate = snapshotDate;
            this.live = live;
            this.approximateStates = approximateStates;
        }

        static <T> Snapshot<T> empty() {
            return new Snapshot<>(Collections.emptyList(), Collections.emptyList(), null, false, Collections.emptyList());
        }
    }

    @Autowired
    StateCLRAsOnDateService stateCLRAsOnDateService;
    @Autowired
    StateCLRService stateCLRService;
    @Autowired
    StateAadharService stateAadharService;
    @Autowired
    MapDigitizationReportService mapDigitizationReportService;
    @Autowired
    StateMAPService stateMAPService;
    @Autowired
    StateMRRService stateMRRService;
    @Autowired
    StateSurveyService stateSurveyService;
    @Autowired
    StateRCMSService stateRCMSService;
    @Autowired
    StateSROService stateSROService;
    @Autowired
    LegacyDigitizationReportService legacyDigitizationReportService;
    @Autowired
    SroModernizationReportService sroModernizationReportService;
    @Autowired
    LegacyRevenueReportService legacyRevenueReportService;
    @Autowired
    LegacyDigitizationReportBuilder legacyDigitizationReportBuilder;
    @Autowired
    SroModernizationReportBuilder sroModernizationReportBuilder;
    @Autowired
    LegacyRevenueReportBuilder legacyRevenueReportBuilder;

    @Autowired
    LinkedAadhaarViewReportBackupRepository aadhaarBackupRepository;
    @Autowired
    MapDigitizationReportBackupRepository mapBackupRepository;
    @Autowired
    MrrViewReportBackupRepository mrrBackupRepository;
    @Autowired
    SurveyResurveyViewReportBackupRepository surveyBackupRepository;
    @Autowired
    RemsReportViewReportBackupRepository rcmsBackupRepository;
    @Autowired
    StateSroViewReportBackupRepository sroBackupRepository;
    @Autowired
    StateRegistrationSystemBackupRepository stateRegistrationSystemBackupRepository;
    @Autowired
    IgrMISDataEntryRepository igrMISDataEntryRepository;
    @Autowired
    DistrictMISDataEntryBackupRepository districtMISDataEntryBackupRepository;

    /** Legacy, SRO modernization and Legacy Revenue reports were added in October 2026. */
    public static final LocalDate REPORTS_AVAILABLE_FROM = LocalDate.of(2026, 10, 1);

    public LocalDate getFirstAvailableDate() {
        return REPORTS_AVAILABLE_FROM;
    }

    public Map<String, String> labelsFor(Component component) {
        switch (component) {
            case CLR: return stateCLRService.createLabels();
            case AADHAAR: return stateAadharService.createLabels();
            case MAP: return stateMAPService.createLabels();
            case MRR: return stateMRRService.createLabels();
            case SURVEY: return stateSurveyService.createLabels();
            case RCMS: return stateRCMSService.createLabels();
            case SRO: return stateSROService.createLabels();
            case LEGACY: return legacyDigitizationReportService.createLabels();
            case SRO_MODERNIZATION: return sroModernizationReportService.createLabels();
            case LEGACY_REVENUE: return legacyRevenueReportService.createLabels();
            default: throw new IllegalArgumentException("Unknown component " + component);
        }
    }

    public Snapshot<?> getReportAsOn(Component component, LocalDate asOnDate) {
        switch (component) {
            case CLR: return getClr(asOnDate);
            case AADHAAR: return getAadhaar(asOnDate);
            case MAP: return getMap(asOnDate);
            case MRR: return getMrr(asOnDate);
            case SURVEY: return getSurvey(asOnDate);
            case RCMS: return getRcms(asOnDate);
            case SRO: return getSro(asOnDate);
            case LEGACY: return getLegacy(asOnDate);
            case SRO_MODERNIZATION: return getSroModernization(asOnDate);
            case LEGACY_REVENUE: return getLegacyRevenue(asOnDate);
            default: throw new IllegalArgumentException("Unknown component " + component);
        }
    }

    private static boolean isLive(LocalDate asOnDate) {
        return !asOnDate.isBefore(LocalDate.now());
    }

    private static <T> Snapshot<T> live(List<T> list, List<T> grandTotal) {
        return new Snapshot<>(list, grandTotal, LocalDate.now(), true, Collections.emptyList());
    }

    private static boolean isGrandTotal(Number id) {
        return id != null && id.longValue() == GRAND_TOTAL_ID;
    }

    /** Copies one backup snapshot into report rows, splitting off the Grand Total row and sorting by state name. */
    private static <B, V> Snapshot<V> fromBackup(LocalDate asOnDate,
                                                 Function<LocalDate, LocalDate> latestDate,
                                                 Function<LocalDate, List<B>> snapshotRows,
                                                 Supplier<V> factory,
                                                 Function<V, Number> idOf,
                                                 Function<V, String> nameOf,
                                                 Consumer<V> format) {
        LocalDate snapshotDate = latestDate.apply(asOnDate);
        if (snapshotDate == null) {
            return Snapshot.empty();
        }
        List<V> states = new ArrayList<>();
        List<V> total = new ArrayList<>();
        for (B backup : snapshotRows.apply(snapshotDate)) {
            V row = factory.get();
            BeanUtils.copyProperties(backup, row);
            format.accept(row);
            (isGrandTotal(idOf.apply(row)) ? total : states).add(row);
        }
        states.sort(Comparator.comparing(nameOf, Comparator.nullsLast(String::compareToIgnoreCase)));
        return new Snapshot<>(states, total, snapshotDate, false, Collections.emptyList());
    }

    private Snapshot<StateClrReportView> getClr(LocalDate asOnDate) {
        StateCLRAsOnDateService.AsOnDateResult r = stateCLRAsOnDateService.getReportAsOn(asOnDate);
        return new Snapshot<>(r.getStateList(), r.getGrandTotal(), r.getSnapshotDate(), r.isLive(), r.getApproximateStates());
    }

    private Snapshot<LinkedAadharViewReport> getAadhaar(LocalDate asOnDate) {
        if (isLive(asOnDate)) {
            return live(stateAadharService.getAllAadhaarFormatList(), stateAadharService.getStateLinkedAadhaarReportsGrandFormateToatal());
        }
        return fromBackup(asOnDate, aadhaarBackupRepository::findLatestBackupDateOnOrBefore,
                aadhaarBackupRepository::findSnapshotByBackupDate, LinkedAadharViewReport::new,
                LinkedAadharViewReport::getStateId, LinkedAadharViewReport::getStateName, r -> {
                    r.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(r.getTotalDistrict()));
                    r.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(r.getTotalTehsils()));
                    r.setFormattingTotalVillages(NumberFormatterUtil.formatWithCommas(r.getTotalVillages()));
                    r.setFormattingvillagesWithRorLinkedAadhaar(NumberFormatterUtil.formatWithCommas(r.getVillagesWithRorLinkedAadhaar()));
                    r.setFormattingvillagesWith100PercentRorLinkedAadhaar(NumberFormatterUtil.formatWithCommas(r.getVillagesWith100PercentRorLinkedAadhaar()));
                    r.setFormattingtotalRor(NumberFormatterUtil.formatWithCommas(r.getTotalRor()));
                    r.setFormattingrorLinkedWithAadhaar(NumberFormatterUtil.formatWithCommas(r.getRorLinkedWithAadhaar()));
                    r.setFormattingrorLinkedWithMobileNumber(NumberFormatterUtil.formatWithCommas(r.getRorLinkedWithMobileNumber()));
                });
    }

    private Snapshot<MapDigitizationReport> getMap(LocalDate asOnDate) {
        if (isLive(asOnDate)) {
            return live(mapDigitizationReportService.getAllMapDigitizationReportFormate(),
                    mapDigitizationReportService.getStateMapReportsGrandToatalFormated());
        }
        return fromBackup(asOnDate, mapBackupRepository::findLatestBackupDateOnOrBefore,
                mapBackupRepository::findSnapshotByBackupDate, MapDigitizationReport::new,
                MapDigitizationReport::getLgdCode, MapDigitizationReport::getStateName, r -> {
                    r.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(r.getTotalDistrict()));
                    r.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(r.getTotalTehsils()));
                    r.setFormattingTotalVillages(NumberFormatterUtil.formatWithCommas(r.getTotalVillages()));
                    r.setFormattingtotalCadastralMaps(NumberFormatterUtil.formatWithCommas(r.getTotalCadastralMaps()));
                    r.setFormattingdigitizedCadastralMaps(NumberFormatterUtil.formatWithCommas(r.getDigitizedCadastralMaps()));
                    r.setFormattingtotalFmbs(NumberFormatterUtil.formatWithCommas(r.getTotalFmbs()));
                    r.setFormattingdigitizedFmbs(NumberFormatterUtil.formatWithCommas(r.getDigitizedFmbs()));
                    r.setFormattingtotalTippans(NumberFormatterUtil.formatWithCommas(r.getTotalTippans()));
                    r.setFormattingdigitizedTippans(NumberFormatterUtil.formatWithCommas(r.getDigitizedTippans()));
                    r.setFormattingvillagesLinkedWithRor(NumberFormatterUtil.formatWithCommas(r.getVillagesLinkedWithRor()));
                    r.setFormattingvillagesGeoreferenced(NumberFormatterUtil.formatWithCommas(r.getVillagesGeoreferenced()));
                    r.setFormattinggeoreferencedMaps(NumberFormatterUtil.formatWithCommas(r.getGeoreferencedMaps()));
                    r.setFormattingtotalMapsFmbTippans(NumberFormatterUtil.formatWithCommas(r.getTotalMapsFmbTippans()));
                    r.setFormattingtotalDigitizedMapsFmbTippans(NumberFormatterUtil.formatWithCommas(r.getTotalDigitizedMapsFmbTippans()));
                    r.setFormattingtotalLandParcels(NumberFormatterUtil.formatWithCommas(r.getTotalLandParcels()));
                    r.setFormattinggeoreferencedLandParcels(NumberFormatterUtil.formatWithCommas(r.getGeoreferencedLandParcels()));
                    r.setFormattingvillagesWithUlipn(NumberFormatterUtil.formatWithCommas(r.getVillagesWithUlipn()));
                    r.setFormattinglandParcelsWithUlipn(NumberFormatterUtil.formatWithCommas(r.getLandParcelsWithUlipn()));
                });
    }

    private Snapshot<MrrViewReport> getMrr(LocalDate asOnDate) {
        if (isLive(asOnDate)) {
            return live(stateMRRService.getAllMrrFormatedList(), stateMRRService.getStateMrrReportsFormatedGrandToatal());
        }
        return fromBackup(asOnDate, mrrBackupRepository::findLatestBackupDateOnOrBefore,
                mrrBackupRepository::findSnapshotByBackupDate, MrrViewReport::new,
                MrrViewReport::getStateId, MrrViewReport::getStateName, r -> {
                    r.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(r.getTotalDistrict()));
                    r.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(r.getTotalTehsils()));
                    r.setFormattingMrrCompleted(NumberFormatterUtil.formatWithCommas(r.getMrrCompleted()));
                    r.setFormattingMrrSanctioned(NumberFormatterUtil.formatWithCommas(r.getMrrSanctioned()));
                    // Not stored in the backup (type differs); same formula as mrr_report_view.
                    r.setMrrCompletedOutOfTotalTehsilsPercent(percent(r.getMrrCompleted(), r.getTotalTehsils()));
                });
    }

    private Snapshot<SurveyResurveyViewReport> getSurvey(LocalDate asOnDate) {
        if (isLive(asOnDate)) {
            return live(stateSurveyService.getAllSurveyResurveyList(), stateSurveyService.getStateSurveyReportsGrandToatal());
        }
        DecimalFormat df = new DecimalFormat("#.####");
        return fromBackup(asOnDate, surveyBackupRepository::findLatestBackupDateOnOrBefore,
                surveyBackupRepository::findSnapshotByBackupDate, SurveyResurveyViewReport::new,
                SurveyResurveyViewReport::getStateId, SurveyResurveyViewReport::getStateName, r -> {
                    r.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(r.getTotalDistrict()));
                    r.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(r.getTotalTehsils()));
                    r.setFormattingTotalVillages(NumberFormatterUtil.formatWithCommas(r.getTotalVillages()));
                    r.setFormattingvillagesDroneFlyingCompleted(NumberFormatterUtil.formatWithCommas(r.getVillagesDroneFlyingCompleted()));
                    r.setFormattingvillagesMap1Generated(NumberFormatterUtil.formatWithCommas(r.getVillagesMap1Generated()));
                    r.setFormattingvillagesDraftMapPublished(NumberFormatterUtil.formatWithCommas(r.getVillagesDraftMapPublished()));
                    r.setFormattingvillagesFinalPromulgationDone(NumberFormatterUtil.formatWithCommas(r.getVillagesFinalPromulgationDone()));
                    r.setFormattingvillagesSurveySanctionNotStarted(NumberFormatterUtil.formatWithCommas(r.getVillagesSurveySanctionNotStarted()));
                    r.setFormatetotalRuralRevenueArea(formatArea(r.getTotalRuralRevenueArea(), df));
                    r.setFomateareaSanctionedForSurvey(formatArea(r.getAreaSanctionedForSurvey(), df));
                    r.setFormateareaDroneFlyingCompleted(formatArea(r.getAreaDroneFlyingCompleted(), df));
                    r.setFormateareaSurveySanctionNotStarted(formatArea(r.getAreaSurveySanctionNotStarted(), df));
                });
    }

    private Snapshot<RcmsReportDTO> getRcms(LocalDate asOnDate) {
        if (isLive(asOnDate)) {
            return live(stateRCMSService.getRcmsStateViwe(), stateRCMSService.getStateRcmsReportsGrandToatal());
        }
        return fromBackup(asOnDate, rcmsBackupRepository::findLatestBackupDateOnOrBefore,
                rcmsBackupRepository::findSnapshotByBackupDate, RcmsReportDTO::new,
                RcmsReportDTO::getStateId, RcmsReportDTO::getStateName, r -> { });
    }

    private Snapshot<SroReportDTO> getSro(LocalDate asOnDate) {
        if (isLive(asOnDate)) {
            return live(stateSROService.getSroStateViwe(), stateSROService.getStateSroReportsGrandToatal());
        }
        Snapshot<SroReportDTO> snapshot = fromBackup(asOnDate, sroBackupRepository::findLatestBackupDateOnOrBefore,
                sroBackupRepository::findSnapshotByBackupDate, SroReportDTO::new,
                SroReportDTO::getStateId, SroReportDTO::getStateName, r -> { });
        if (snapshot.getSnapshotDate() == null) {
            return snapshot;
        }

        // Legacy-record years and SROs integrated with land records are not in the report backup;
        // take them from the registration data-entry history as on the date.
        Map<Long, AsOnDateEntryResolver.Resolved> entries = AsOnDateEntryResolver.resolve(
                stateRegistrationSystemBackupRepository.findSroAsOnDateFieldHistory(),
                igrMISDataEntryRepository.findSroAsOnDateFieldsCurrent(),
                asOnDate);
        List<String> approximateStates = new ArrayList<>();
        int integratedTotal = 0;
        for (SroReportDTO row : snapshot.getStateList()) {
            AsOnDateEntryResolver.Resolved e = entries.get(row.getStateId());
            Integer integrated = 0;
            if (e != null) {
                row.setLegacyRecordsAvailableFromYearSRO(AsOnDateEntryResolver.toUtilDate(e.value(0)));
                row.setLegacyRecordsAvailableFromYearPublic(AsOnDateEntryResolver.toUtilDate(e.value(1)));
                integrated = AsOnDateEntryResolver.toInteger(e.value(2));
                if (e.isApproximate()) {
                    approximateStates.add(row.getStateName());
                }
            }
            row.setNumberOfSROsIntegratedWithLandRecords(integrated != null ? integrated : 0);
            integratedTotal += row.getNumberOfSROsIntegratedWithLandRecords();
        }
        for (SroReportDTO total : snapshot.getGrandTotal()) {
            total.setNumberOfSROsIntegratedWithLandRecords(integratedTotal);
        }
        return new Snapshot<>(snapshot.getStateList(), snapshot.getGrandTotal(), snapshot.getSnapshotDate(), false, approximateStates);
    }

    private Snapshot<LegacyDigitizationReport> getLegacy(LocalDate asOnDate) {
        if (isLive(asOnDate)) {
            return live(legacyDigitizationReportService.getAllFormattedList(),
                    legacyDigitizationReportService.getGrandTotalFormatted());
        }
        Snapshot<MrrViewReport> geo = fromBackup(asOnDate, mrrBackupRepository::findLatestBackupDateOnOrBefore,
                mrrBackupRepository::findSnapshotByBackupDate, MrrViewReport::new,
                MrrViewReport::getStateId, MrrViewReport::getStateName, r -> { });
        if (geo.getSnapshotDate() == null) {
            return Snapshot.empty();
        }
        List<LegacyDigitizationReport> states = new ArrayList<>();
        List<LegacyDigitizationReport> total = new ArrayList<>();
        for (MrrViewReport row : geo.getStateList()) {
            states.add(legacyDigitizationReportBuilder.fromMrrState(row));
        }
        for (MrrViewReport row : geo.getGrandTotal()) {
            total.add(legacyDigitizationReportBuilder.fromMrrState(row));
        }
        List<LegacyDigitizationReport> all = new ArrayList<>();
        all.addAll(states);
        all.addAll(total);
        legacyDigitizationReportBuilder.enrichStateReports(all,
                districtMISDataEntryBackupRepository.sumLegacyDigitizationByStateAsOn(asOnDate),
                districtMISDataEntryBackupRepository.sumLegacyDigitizationNationalAsOn(asOnDate));
        return new Snapshot<>(states, total, geo.getSnapshotDate(), false, Collections.emptyList());
    }

    private Snapshot<SroModernizationReport> getSroModernization(LocalDate asOnDate) {
        if (isLive(asOnDate)) {
            return live(sroModernizationReportService.getAllFormattedList(),
                    sroModernizationReportService.getGrandTotalFormatted());
        }
        Snapshot<SroReportDTO> geo = fromBackup(asOnDate, sroBackupRepository::findLatestBackupDateOnOrBefore,
                sroBackupRepository::findSnapshotByBackupDate, SroReportDTO::new,
                SroReportDTO::getStateId, SroReportDTO::getStateName, r -> { });
        if (geo.getSnapshotDate() == null) {
            return Snapshot.empty();
        }
        List<SroModernizationReport> states = new ArrayList<>();
        List<SroModernizationReport> total = new ArrayList<>();
        for (SroReportDTO row : geo.getStateList()) {
            states.add(sroModernizationReportBuilder.fromSroView(row));
        }
        for (SroReportDTO row : geo.getGrandTotal()) {
            total.add(sroModernizationReportBuilder.fromSroView(row));
        }
        List<SroModernizationReport> all = new ArrayList<>();
        all.addAll(states);
        all.addAll(total);
        sroModernizationReportBuilder.enrichStateReports(all,
                districtMISDataEntryBackupRepository.sumSroModernizationByStateAsOn(asOnDate),
                districtMISDataEntryBackupRepository.sumSroModernizationNationalAsOn(asOnDate));
        return new Snapshot<>(states, total, geo.getSnapshotDate(), false, Collections.emptyList());
    }

    private Snapshot<LegacyRevenueReport> getLegacyRevenue(LocalDate asOnDate) {
        if (isLive(asOnDate)) {
            return live(legacyRevenueReportService.getAllFormattedList(),
                    legacyRevenueReportService.getGrandTotalFormatted());
        }
        Snapshot<MrrViewReport> geo = fromBackup(asOnDate, mrrBackupRepository::findLatestBackupDateOnOrBefore,
                mrrBackupRepository::findSnapshotByBackupDate, MrrViewReport::new,
                MrrViewReport::getStateId, MrrViewReport::getStateName, r -> { });
        if (geo.getSnapshotDate() == null) {
            return Snapshot.empty();
        }
        List<LegacyRevenueReport> states = new ArrayList<>();
        List<LegacyRevenueReport> total = new ArrayList<>();
        for (MrrViewReport row : geo.getStateList()) {
            states.add(legacyRevenueReportBuilder.fromMrrState(row));
        }
        for (MrrViewReport row : geo.getGrandTotal()) {
            total.add(legacyRevenueReportBuilder.fromMrrState(row));
        }
        List<LegacyRevenueReport> all = new ArrayList<>();
        all.addAll(states);
        all.addAll(total);
        legacyRevenueReportBuilder.enrichStateReports(all,
                districtMISDataEntryBackupRepository.sumLegacyRevenueByStateAsOn(asOnDate),
                districtMISDataEntryBackupRepository.sumLegacyRevenueNationalAsOn(asOnDate));
        return new Snapshot<>(states, total, geo.getSnapshotDate(), false, Collections.emptyList());
    }

    private static BigDecimal percent(Integer part, Integer whole) {
        if (part == null || whole == null || whole <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(part)
                .divide(BigDecimal.valueOf(whole), 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static String formatArea(Double value, DecimalFormat df) {
        if (value == null || value == 0) {
            return "0.00";
        }
        return df.format(value);
    }
}
