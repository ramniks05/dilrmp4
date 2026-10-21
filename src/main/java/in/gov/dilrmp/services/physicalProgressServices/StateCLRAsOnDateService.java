package in.gov.dilrmp.services.physicalProgressServices;

import in.gov.dilrmp.Scheduler.models.StateClrReportViewBackup;
import in.gov.dilrmp.Scheduler.repositories.StateClrReportViewBackupRepository;
import in.gov.dilrmp.Scheduler.repositories.StateMISDataEntryBackupRepository;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.StateMISDataEntryRepository;
import in.gov.dilrmp.utils.AsOnDateEntryResolver;
import in.gov.dilrmp.utils.NumberFormatterUtil;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * State CLR report as on a past date.
 * Numbers come from the nightly state_clr_report_view_backup snapshot (latest snapshot on or before the date).
 * Yes/No columns are not stored in that snapshot, so they are taken from the state data-entry history:
 * the latest statemisdata_entry_backup row on or before the date, else the current entry if it has not
 * been edited since. When neither is known, the earliest known answer is shown with a "*".
 */
@Service
public class StateCLRAsOnDateService {

    private static final long GRAND_TOTAL_STATE_ID = 999L;
    private static final int FLAG_COUNT = 9;

    @Autowired
    StateClrReportViewBackupRepository stateClrReportViewBackupRepository;
    @Autowired
    StateMISDataEntryBackupRepository stateMISDataEntryBackupRepository;
    @Autowired
    StateMISDataEntryRepository stateMISDataEntryRepository;
    @Autowired
    StateCLRService stateCLRService;

    // Same order as the flag columns in findClrFlagHistory() / findClrFlagsCurrent().
    private static final List<BiConsumer<StateClrReportView, String>> FLAG_SETTERS = List.of(
            StateClrReportView::setRorAvailableOnline,
            StateClrReportView::setDigitallySignedRorAvailable,
            StateClrReportView::setDigitallySignedRorLegallyValid,
            StateClrReportView::setOnlineMutationFacility,
            StateClrReportView::setAutoTriggerMutation,
            StateClrReportView::setBankRedFlagMortgageInLandRecords,
            StateClrReportView::setLandRecordsOnlineFromRegistrationSystem,
            StateClrReportView::setRevenueCourtProceedingsPaperless,
            StateClrReportView::setCaseFilingRedFlaggedInLandRecordsFromCivilCourts);

    private static final List<Function<StateClrReportView, String>> FLAG_GETTERS = List.of(
            StateClrReportView::getRorAvailableOnline,
            StateClrReportView::getDigitallySignedRorAvailable,
            StateClrReportView::getDigitallySignedRorLegallyValid,
            StateClrReportView::getOnlineMutationFacility,
            StateClrReportView::getAutoTriggerMutation,
            StateClrReportView::getBankRedFlagMortgageInLandRecords,
            StateClrReportView::getLandRecordsOnlineFromRegistrationSystem,
            StateClrReportView::getRevenueCourtProceedingsPaperless,
            StateClrReportView::getCaseFilingRedFlaggedInLandRecordsFromCivilCourts);

    @Getter
    public static class AsOnDateResult {
        private final List<StateClrReportView> stateList;
        private final List<StateClrReportView> grandTotal;
        private final LocalDate snapshotDate;
        private final boolean live;
        private final List<String> approximateStates;

        AsOnDateResult(List<StateClrReportView> stateList, List<StateClrReportView> grandTotal,
                       LocalDate snapshotDate, boolean live, List<String> approximateStates) {
            this.stateList = stateList;
            this.grandTotal = grandTotal;
            this.snapshotDate = snapshotDate;
            this.live = live;
            this.approximateStates = approximateStates;
        }
    }

    public LocalDate getFirstAvailableDate() {
        return stateClrReportViewBackupRepository.findFirstBackupDate();
    }

    public AsOnDateResult getReportAsOn(LocalDate asOnDate) {
        if (!asOnDate.isBefore(LocalDate.now())) {
            List<StateClrReportView> list = stateCLRService.getClrStateViwe();
            List<StateClrReportView> total = stateCLRService.getStateClrReportsGrandToatal();
            list.forEach(StateCLRAsOnDateService::normaliseLiveFlags);
            return new AsOnDateResult(list, total, LocalDate.now(), true, Collections.emptyList());
        }

        LocalDate snapshotDate = stateClrReportViewBackupRepository.findLatestBackupDateOnOrBefore(asOnDate);
        if (snapshotDate == null) {
            return new AsOnDateResult(Collections.emptyList(), Collections.emptyList(), null, false, Collections.emptyList());
        }

        List<StateClrReportView> states = new ArrayList<>();
        StateClrReportView total = null;
        for (StateClrReportViewBackup backup : stateClrReportViewBackupRepository.findSnapshotByBackupDate(snapshotDate)) {
            StateClrReportView row = new StateClrReportView();
            BeanUtils.copyProperties(backup, row);
            formatNumbers(row);
            if (Long.valueOf(GRAND_TOTAL_STATE_ID).equals(backup.getStateId())) {
                total = row;
            } else {
                states.add(row);
            }
        }
        states.sort(Comparator.comparing(StateClrReportView::getStateName, Comparator.nullsLast(String::compareToIgnoreCase)));

        List<String> approximateStates = applyFlagsAsOn(states, asOnDate);

        List<StateClrReportView> grandTotal = new ArrayList<>();
        if (total != null) {
            for (int i = 0; i < FLAG_COUNT; i++) {
                Function<StateClrReportView, String> getter = FLAG_GETTERS.get(i);
                long yes = states.stream().map(getter).filter(v -> v != null && v.startsWith("Yes")).count();
                FLAG_SETTERS.get(i).accept(total, String.valueOf(yes));
            }
            grandTotal.add(total);
        }
        return new AsOnDateResult(states, grandTotal, snapshotDate, false, approximateStates);
    }

    private List<String> applyFlagsAsOn(List<StateClrReportView> states, LocalDate asOnDate) {
        Map<Long, AsOnDateEntryResolver.Resolved> resolved = AsOnDateEntryResolver.resolve(
                stateMISDataEntryBackupRepository.findClrFlagHistory(),
                stateMISDataEntryRepository.findClrFlagsCurrent(),
                asOnDate);

        List<String> approximateStates = new ArrayList<>();
        for (StateClrReportView state : states) {
            AsOnDateEntryResolver.Resolved source = resolved.get(state.getStateId());
            boolean approximate = source != null && source.isApproximate();
            String suffix = approximate ? "*" : "";
            for (int i = 0; i < FLAG_COUNT; i++) {
                boolean yes = source != null && Boolean.TRUE.equals(source.value(i));
                FLAG_SETTERS.get(i).accept(state, (yes ? "Yes" : "No") + suffix);
            }
            if (approximate) {
                approximateStates.add(state.getStateName());
            }
        }
        return approximateStates;
    }

    private static void normaliseLiveFlags(StateClrReportView row) {
        for (int i = 0; i < FLAG_COUNT; i++) {
            String v = FLAG_GETTERS.get(i).apply(row);
            FLAG_SETTERS.get(i).accept(row, "YES".equalsIgnoreCase(v) ? "Yes" : "No");
        }
    }

    private static void formatNumbers(StateClrReportView clr) {
        clr.setFormattingTotalRor(NumberFormatterUtil.formatWithCommas(clr.getTotalRor()));
        clr.setFormattingvillagesClrCompleted(NumberFormatterUtil.formatWithCommas(clr.getVillagesClrCompleted()));
        clr.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(clr.getTotalDistrict()));
        clr.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(clr.getTotalTehsils()));
        clr.setFormattingTotalVillages(NumberFormatterUtil.formatWithCommas(clr.getTotalVillages()));
        clr.setFormattingTotalLandOwners(NumberFormatterUtil.formatWithCommas(clr.getTotalLandOwners()));
        clr.setFormattingDistrictsWithBankRedFlagMortgage(NumberFormatterUtil.formatWithCommas(
                clr.getDistrictsWithBankRedFlagMortgage() != null ? clr.getDistrictsWithBankRedFlagMortgage().intValue() : 0));
        clr.setFormattingBankBranchesWithRedFlagMortgage(NumberFormatterUtil.formatWithCommas(clr.getBankBranchesWithRedFlagMortgage()));
        clr.setFormattingRorComputerized(NumberFormatterUtil.formatWithCommas(clr.getRorComputerized()));
        clr.setFormattingTotalMaleLandOwners(NumberFormatterUtil.formatWithCommas(clr.getTotalMaleLandOwners()));
        clr.setFormattingTotalFemaleLandOwners(NumberFormatterUtil.formatWithCommas(clr.getTotalFemaleLandOwners()));
        clr.setFormattingTotalOwners(NumberFormatterUtil.formatWithCommas(clr.getTotalOwners()));
    }
}
