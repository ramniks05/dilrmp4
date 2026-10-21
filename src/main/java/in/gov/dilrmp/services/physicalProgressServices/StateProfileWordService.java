package in.gov.dilrmp.services.physicalProgressServices;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.cna.DilrmpComponentExpenditureStatus;
import in.gov.dilrmp.models.naksha.CapacityBuilding;
import in.gov.dilrmp.models.naksha.NakshaMISDataEntry;
import in.gov.dilrmp.models.naksha.NakshaMISDataEntrySolrAndVender;
import in.gov.dilrmp.models.naksha.ULBMaster;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.legacy.LegacyDigitizationReport;
import in.gov.dilrmp.models.reportDTO.legacyRevenue.LegacyRevenueReport;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.models.reportDTO.profile.StateProfileWordInput;
import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.models.reportDTO.sroModernization.SroModernizationReport;
import in.gov.dilrmp.repositories.naksha.CapacityBuildingRepository;
import in.gov.dilrmp.repositories.naksha.NakshaMISDataRepository;
import in.gov.dilrmp.repositories.naksha.NakshaMISDataVenderRepository;
import in.gov.dilrmp.repositories.naksha.ULBMsterRepository;
import in.gov.dilrmp.services.administrativeBoundry.StateService;
import in.gov.dilrmp.services.cna.DilrmpComponentExpenditureStatusRepository;
import in.gov.dilrmp.services.naksha.GroundTruthingDetailService;
import in.gov.dilrmp.services.physicalProgressServices.StateReportsAsOnDateService.Component;
import in.gov.dilrmp.services.physicalProgressServices.StateReportsAsOnDateService.Snapshot;
import in.gov.dilrmp.utils.CompletionStatus;
import in.gov.dilrmp.utils.NumberFormatterUtil;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Builds the editable State Profile (.docx) in the layout DoLR uses for state reviews.
 * Physical figures come from MIS as on the chosen date; everything else comes from the entry form
 * and is left blank when not entered.
 */
@Service
public class StateProfileWordService {

    private static final Logger logger = LoggerFactory.getLogger(StateProfileWordService.class);
    private static final DateTimeFormatter DOT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter LONG = DateTimeFormatter.ofPattern("d MMMM yyyy");
    private static final long ONE_CRORE = 10_000_000L;
    private static final String HEADER_FILL = "D9E2F3";
    private static final String FONT = "Calibri";
    private static final int NAKSHA_EXPENDITURE_SERIAL_NO = 6;
    private static final Set<Integer> DONE = Set.of(
            CompletionStatus.COMPLETED.getCode(), CompletionStatus.COMPLETED_POST_CORRECTION.getCode(),
            CompletionStatus.ACCEPTED.getCode(), CompletionStatus.ACCEPTED_AFTER_CORRECTION.getCode(),
            CompletionStatus.RE_FLY_COMPLETED.getCode());

    @Autowired
    StateReportsAsOnDateService stateReportsAsOnDateService;
    @Autowired
    StateService stateService;
    @Autowired
    GroundTruthingDetailService groundTruthingDetailService;
    @Autowired
    ULBMsterRepository ulbMsterRepository;
    @Autowired
    NakshaMISDataRepository nakshaMISDataRepository;
    @Autowired
    NakshaMISDataVenderRepository nakshaMISDataVenderRepository;
    @Autowired
    CapacityBuildingRepository capacityBuildingRepository;
    @Autowired
    DilrmpComponentExpenditureStatusRepository dilrmpComponentExpenditureStatusRepository;

    public String stateName(Long stateId) {
        if (stateId == null) {
            return "";
        }
        for (State state : stateService.findAllOrderByStateName()) {
            if (stateId.equals(state.getId())) {
                return state.getName();
            }
        }
        return "";
    }

    /** Everything the profile takes from MIS for the state and date; shown read-only on screen and written to the Word file. */
    public MisData misData(long stateId, LocalDate asOnDate) {
        MisData mis = new MisData();
        mis.stateName = toTitleCase(stateName(stateId));
        mis.asOnDate = asOnDate;
        mis.rows = physicalRows(stateId, asOnDate);
        mis.lagging = lagging(mis.rows);
        StateClrReportView clrState = stateRow(stateReportsAsOnDateService.getReportAsOn(Component.CLR, asOnDate),
                StateClrReportView::getStateId, stateId);
        mis.districts = count(clrState == null ? null : clrState.getTotalDistrict());
        mis.tehsils = count(clrState == null ? null : clrState.getTotalTehsils());
        mis.villages = count(clrState == null ? null : clrState.getTotalVillages());

        List<ULBMaster> ulbMasters = ulbMasters(stateId);
        List<NakshaMISDataEntrySolrAndVender> vendors = vendorEntries(ulbMasters);
        mis.ulbs = nakshaUlbs(stateId, mis.stateName, ulbMasters);
        mis.ulbText = defaultUlbText(mis.stateName, mis.ulbs);
        mis.flying = activityStatus(flyingStatuses(vendors), "Completed");
        mis.roversTeams = roversTeamsText(stateId);
        mis.ori = activityStatus(statuses(vendors, NakshaMISDataEntrySolrAndVender::getOriCumulativestatus), "Generated");
        mis.feature2d = activityStatus(featureStatuses(vendors, false), "Created");
        mis.feature3d = activityStatus(featureStatuses(vendors, true), "Generated");
        mis.groundTruthing = groundTruthingText(mis.ulbs);
        mis.capacityBuilding = capacityBuildingText(stateId);
        String[] release = nakshaRelease(stateId, asOnDate);
        mis.sanctionedBudget = release[0];
        mis.releaseYear = release[1];
        mis.amountReleased = release[2];
        return mis;
    }

    public byte[] build(StateProfileWordInput in) throws IOException {
        long stateId = in.getStateId();
        LocalDate asOnDate = in.getAsOnDate() != null ? in.getAsOnDate() : LocalDate.now();
        MisData mis = misData(stateId, asOnDate);
        String state = mis.stateName;
        List<ComponentRow> rows = mis.rows;
        List<String[]> ulbs = mis.ulbs;

        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            text(doc, "Department of Land Resources", true, 16, ParagraphAlignment.CENTER);
            text(doc, "State: " + state, true, 14, ParagraphAlignment.CENTER);
            text(doc, "(" + asOnDate.format(LONG) + ")", false, 12, ParagraphAlignment.CENTER);

            heading(doc, "1. State level information on Schemes");
            text(doc, state + " has a total of " + mis.districts + " districts, " + mis.tehsils + " Tehsils, "
                    + mis.villages + " Villages", false, 11, ParagraphAlignment.LEFT);

            heading(doc, "2. Digital India Land Records Modernisation programme (DILRMP)");
            labelled(doc, "2.1 Financial Progress: ", "Fund release Status (₹ in crore)");
            fundTable(doc, in);
            labelled(doc, "2.2 Unspent funds with State Government: ",
                    isBlank(in.getUnspentFunds()) ? "" : "₹" + in.getUnspentFunds().trim() + " Crore"
                            + (isBlank(in.getUnspentAsPerPfmsDate()) ? "" : " (as per PFMS on " + in.getUnspentAsPerPfmsDate().trim() + ")"));
            labelled(doc, "2.3 Status of pending Utilization Certificate (UC): ",
                    isBlank(in.getPendingUc()) ? "" : "UC is pending for an amount of ₹" + in.getPendingUc().trim() + " Crore");
            labelled(doc, "2.4 Physical Progress: ", "As per MIS as on " + asOnDate.format(DOT));
            physicalTable(doc, state, rows, in.getReasons());

            heading(doc, "3. Status of Other Components under DILRMP");
            labelled(doc, "3.1 National Generic Document Registration System (NGDRS) / e-Registration: ", value(in.getNgdrsStatus()));
            labelled(doc, "3.2 Revenue Court Case Management System (RCCMS): ", value(in.getRccmsStatus()));
            labelled(doc, "3.3 Introduction of Gender Column in RoR: ", value(in.getGenderColumnStatus()));
            labelled(doc, "3.4 Bharat Land Stack: ", value(in.getLandStackStatus()));

            heading(doc, "4. NAKSHA Programme");
            multiline(doc, mis.ulbText);
            text(doc, "Physical Status", true, 11, ParagraphAlignment.LEFT);
            labelled(doc, "1. Aerial Flying: ", mis.flying);
            labelled(doc, "2. Rovers, SPMU & Teams: ", mis.roversTeams);
            labelled(doc, "3. Ortho Rectified Imagery (ORI): ", mis.ori);
            labelled(doc, "4. 2D Feature Extraction: ", mis.feature2d);
            labelled(doc, "5. 3D Feature Extraction: ", mis.feature3d);
            labelled(doc, "6. Ground Truthing: ", mis.groundTruthing);
            labelled(doc, "    RoR Tagging: ", value(in.getNakshaRorTagging()));
            labelled(doc, "7. Capacity Building: ", mis.capacityBuilding);
            labelled(doc, "8. Total Sanctioned Budget: ", mis.sanctionedBudget);
            labelled(doc, mis.getReleaseLabel() + ": ", mis.amountReleased);
            labelled(doc, "10. Amount Utilized (from DILRMP Fund balance): ", crore(in.getNakshaAmountUtilized()));
            labelled(doc, "11. Amount Recommended in SASCI: ", value(in.getNakshaSasci()));
            if (!isBlank(in.getNakshaPhysicalStatus())) {
                numbered(doc, in.getNakshaPhysicalStatus(), 12);
            }

            heading(doc, "5. Any Similar Scheme Funded by the State Government, with details of funding");
            numbered(doc, in.getSimilarScheme(), 1);

            heading(doc, "6. Issues Flagged by the State Government");
            numbered(doc, in.getIssuesByState(), 1);

            heading(doc, "7. Issues Flagged by the DoLR");
            text(doc, "(a) Digital India Land Records Modernization Programme (DILRMP)", true, 11, ParagraphAlignment.LEFT);
            text(doc, "(i) Financial Progress", true, 11, ParagraphAlignment.LEFT);
            numbered(doc, in.getIssuesByDolrFinancial(), 1);
            text(doc, "(ii) Physical Progress", true, 11, ParagraphAlignment.LEFT);
            text(doc, "The State of " + state + " needs to improve physical progress under the following DILRMP 2.0 components:",
                    false, 11, ParagraphAlignment.LEFT);
            laggingTable(doc, mis.lagging);
            numbered(doc, in.getIssuesByDolrPhysical(), 1);
            text(doc, "(b) NAKSHA Programme", true, 11, ParagraphAlignment.LEFT);
            numbered(doc, in.getIssuesByDolrNaksha(), 1);
            text(doc, "ULB-wise status and prescribed timelines:", false, 11, ParagraphAlignment.LEFT);
            nakshaTable(doc, ulbs, in);

            doc.write(out);
            return out.toByteArray();
        }
    }

    // ---------------------------------------------------------------- MIS figures

    private List<ComponentRow> physicalRows(long stateId, LocalDate asOnDate) {
        Snapshot<?> clr = stateReportsAsOnDateService.getReportAsOn(Component.CLR, asOnDate);
        Snapshot<?> map = stateReportsAsOnDateService.getReportAsOn(Component.MAP, asOnDate);
        Snapshot<?> sro = stateReportsAsOnDateService.getReportAsOn(Component.SRO, asOnDate);
        Snapshot<?> mrr = stateReportsAsOnDateService.getReportAsOn(Component.MRR, asOnDate);
        Snapshot<?> aadhaar = stateReportsAsOnDateService.getReportAsOn(Component.AADHAAR, asOnDate);
        Snapshot<?> rcms = stateReportsAsOnDateService.getReportAsOn(Component.RCMS, asOnDate);
        Snapshot<?> legacyRevenue = stateReportsAsOnDateService.getReportAsOn(Component.LEGACY_REVENUE, asOnDate);
        Snapshot<?> legacy = stateReportsAsOnDateService.getReportAsOn(Component.LEGACY, asOnDate);
        Snapshot<?> sroModern = stateReportsAsOnDateService.getReportAsOn(Component.SRO_MODERNIZATION, asOnDate);

        StateClrReportView clrS = stateRow(clr, StateClrReportView::getStateId, stateId);
        StateClrReportView clrN = totalRow(clr);
        MapDigitizationReport mapS = stateRow(map, MapDigitizationReport::getLgdCode, stateId);
        MapDigitizationReport mapN = totalRow(map);
        SroReportDTO sroS = stateRow(sro, SroReportDTO::getStateId, stateId);
        SroReportDTO sroN = totalRow(sro);
        MrrViewReport mrrS = stateRow(mrr, MrrViewReport::getStateId, stateId);
        MrrViewReport mrrN = totalRow(mrr);
        LinkedAadharViewReport aadS = stateRow(aadhaar, LinkedAadharViewReport::getStateId, stateId);
        LinkedAadharViewReport aadN = totalRow(aadhaar);
        RcmsReportDTO rcmsS = stateRow(rcms, RcmsReportDTO::getStateId, stateId);
        RcmsReportDTO rcmsN = totalRow(rcms);
        LegacyRevenueReport revS = stateRow(legacyRevenue, LegacyRevenueReport::getStateId, stateId);
        LegacyRevenueReport revN = totalRow(legacyRevenue);
        LegacyDigitizationReport legS = stateRow(legacy, LegacyDigitizationReport::getStateId, stateId);
        LegacyDigitizationReport legN = totalRow(legacy);
        SroModernizationReport smS = stateRow(sroModern, SroModernizationReport::getStateId, stateId);
        SroModernizationReport smN = totalRow(sroModern);

        List<ComponentRow> rows = new ArrayList<>();
        rows.add(new ComponentRow("Computerization of Record of Right (RoRs)", "Number of RoRs",
                fig(clrN, StateClrReportView::getTotalRor, StateClrReportView::getRorComputerized),
                fig(clrS, StateClrReportView::getTotalRor, StateClrReportView::getRorComputerized)));
        rows.add(new ComponentRow("Digitization of Cadastral Maps", "Number of Cadastral Maps",
                fig(mapN, MapDigitizationReport::getTotalCadastralMaps, MapDigitizationReport::getDigitizedCadastralMaps),
                fig(mapS, MapDigitizationReport::getTotalCadastralMaps, MapDigitizationReport::getDigitizedCadastralMaps)));
        rows.add(new ComponentRow("Integration of Cadastral Map with RoR", "Number of Villages",
                fig(mapN, MapDigitizationReport::getTotalVillages, MapDigitizationReport::getVillagesLinkedWithRor),
                fig(mapS, MapDigitizationReport::getTotalVillages, MapDigitizationReport::getVillagesLinkedWithRor)));
        rows.add(new ComponentRow("Computerization of Registration", "Number of SROs",
                fig(sroN, SroReportDTO::getNumberOfSROsInState, SroReportDTO::getNumberOfSROsUsingOnlineRegistration),
                fig(sroS, SroReportDTO::getNumberOfSROsInState, SroReportDTO::getNumberOfSROsUsingOnlineRegistration)));
        rows.add(new ComponentRow("Modern Record Room", "Number of Tehsils (Sanctioned)",
                fig(mrrN, MrrViewReport::getMrrSanctioned, MrrViewReport::getMrrCompleted),
                fig(mrrS, MrrViewReport::getMrrSanctioned, MrrViewReport::getMrrCompleted)));
        rows.add(new ComponentRow("ULPIN", "Number of land parcels",
                fig(mapN, MapDigitizationReport::getTotalLandParcels, MapDigitizationReport::getLandParcelsWithUlipn),
                fig(mapS, MapDigitizationReport::getTotalLandParcels, MapDigitizationReport::getLandParcelsWithUlipn)));
        rows.add(new ComponentRow("Consent based integration of Aadhaar with Land Records", "Number of RoRs",
                fig(aadN, LinkedAadharViewReport::getTotalRor, LinkedAadharViewReport::getRorLinkedWithAadhaar),
                fig(aadS, LinkedAadharViewReport::getTotalRor, LinkedAadharViewReport::getRorLinkedWithAadhaar)));
        rows.add(new ComponentRow("Mobile seeding in Land Records", "Number of RoRs",
                fig(aadN, LinkedAadharViewReport::getTotalRor, LinkedAadharViewReport::getRorLinkedWithMobileNumber),
                fig(aadS, LinkedAadharViewReport::getTotalRor, LinkedAadharViewReport::getRorLinkedWithMobileNumber)));
        rows.add(genderRow(clrN, clrS));
        rows.add(new ComponentRow("Computerization of Revenue Courts", "Number of Revenue Courts",
                fig(rcmsN, RcmsReportDTO::getTotalRevenueCourts, RcmsReportDTO::getRevenueCourtsComputerized),
                fig(rcmsS, RcmsReportDTO::getTotalRevenueCourts, RcmsReportDTO::getRevenueCourtsComputerized)));
        rows.add(new ComponentRow("Legacy Revenue Record Digitisation", "No. of Pages",
                fig(revN, LegacyRevenueReport::getTotalPages, LegacyRevenueReport::getTotalDigitisedPages),
                fig(revS, LegacyRevenueReport::getTotalPages, LegacyRevenueReport::getTotalDigitisedPages)));
        rows.add(new ComponentRow("Legacy Registered Document Digitisation", "No. of Pages",
                fig(legN, LegacyDigitizationReport::getLegacyTotalPages, LegacyDigitizationReport::getLegacyTotalDigitisedPages),
                fig(legS, LegacyDigitizationReport::getLegacyTotalPages, LegacyDigitizationReport::getLegacyTotalDigitisedPages)));
        rows.add(new ComponentRow("Modernisation of SROs", "No. of SRO Offices",
                fig(smN, SroModernizationReport::getTotalSros, SroModernizationReport::getSrosModernisedTotal),
                fig(smS, SroModernizationReport::getTotalSros, SroModernizationReport::getSrosModernisedTotal)));
        return rows;
    }

    private static ComponentRow genderRow(StateClrReportView national, StateClrReportView state) {
        ComponentRow row = new ComponentRow("Availability of Gender based land ownership as per MIS database",
                "Number of land owners",
                new Figure(national == null ? null : national.getTotalLandOwners(), null),
                new Figure(state == null ? null : state.getTotalLandOwners(), null));
        row.nationalDoneText = genderText(national);
        row.stateDoneText = genderText(state);
        row.excludeFromLagging = true;
        return row;
    }

    private static String genderText(StateClrReportView row) {
        if (row == null || (row.getTotalMaleLandOwners() == null && row.getTotalFemaleLandOwners() == null)) {
            return "";
        }
        return "Male- " + amount(row.getTotalMaleLandOwners()) + " / Female- " + amount(row.getTotalFemaleLandOwners());
    }

    private static <T> Figure fig(T row, Function<T, Integer> total, Function<T, Integer> done) {
        if (row == null) {
            return new Figure(null, null);
        }
        return new Figure(total.apply(row), done.apply(row));
    }

    @SuppressWarnings("unchecked")
    private static <T> T stateRow(Snapshot<?> snapshot, Function<T, Number> idOf, long stateId) {
        if (snapshot == null || snapshot.getStateList() == null) {
            return null;
        }
        for (Object o : snapshot.getStateList()) {
            T row = (T) o;
            Number id = idOf.apply(row);
            if (id != null && id.longValue() == stateId) {
                return row;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T totalRow(Snapshot<?> snapshot) {
        if (snapshot == null || snapshot.getGrandTotal() == null || snapshot.getGrandTotal().isEmpty()) {
            return null;
        }
        return (T) snapshot.getGrandTotal().get(0);
    }

    private static List<ComponentRow> lagging(List<ComponentRow> rows) {
        List<ComponentRow> lagging = new ArrayList<>();
        for (ComponentRow row : rows) {
            BigDecimal pct = row.state.percent();
            if (!row.excludeFromLagging && pct != null && pct.compareTo(BigDecimal.valueOf(100)) < 0) {
                lagging.add(row);
            }
        }
        return lagging;
    }

    // ---------------------------------------------------------------- NAKSHA (read only)

    private List<ULBMaster> ulbMasters(long stateId) {
        try {
            return ulbMsterRepository.findByStateId(stateId);
        } catch (RuntimeException e) {
            logger.warn("Could not read NAKSHA ULBs for state {}", stateId, e);
            return Collections.emptyList();
        }
    }

    private List<NakshaMISDataEntrySolrAndVender> vendorEntries(List<ULBMaster> ulbMasters) {
        List<Long> ids = new ArrayList<>();
        for (ULBMaster ulb : ulbMasters) {
            if (ulb.getId() != null) {
                ids.add(ulb.getId());
            }
        }
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return nakshaMISDataVenderRepository.findByUlbMasterIdIn(ids);
        } catch (RuntimeException e) {
            logger.warn("Could not read NAKSHA survey progress", e);
            return Collections.emptyList();
        }
    }

    private static List<Integer> statuses(List<NakshaMISDataEntrySolrAndVender> vendors,
                                          Function<NakshaMISDataEntrySolrAndVender, Integer> status) {
        List<Integer> list = new ArrayList<>();
        for (NakshaMISDataEntrySolrAndVender v : vendors) {
            list.add(status.apply(v));
        }
        return list;
    }

    /**
     * 2D extraction is done for Tech 1 (nadir) ULBs and 3D for Tech 2/3 (oblique) ULBs, so a "Not Started" 3D status
     * on a Tech 1 ULB means not applicable; it counts only once 3D work has actually begun there.
     */
    private static List<Integer> featureStatuses(List<NakshaMISDataEntrySolrAndVender> vendors, boolean threeD) {
        List<Integer> list = new ArrayList<>();
        for (NakshaMISDataEntrySolrAndVender v : vendors) {
            boolean tech1 = v.getTechnology() != null && v.getTechnology().replace(" ", "").equalsIgnoreCase("Tech1");
            if (!threeD) {
                if (tech1) {
                    list.add(v.getTwoDfeatureextractionStatus());
                }
                continue;
            }
            Integer s = v.getThreeDFeatextrStatus();
            if (!tech1 || (s != null && s != CompletionStatus.NOT_STARTED.getCode())) {
                list.add(s);
            }
        }
        return list;
    }

    /** A ULB's flying is complete when every technology it is flown with is complete. */
    private static List<Integer> flyingStatuses(List<NakshaMISDataEntrySolrAndVender> vendors) {
        List<Integer> list = new ArrayList<>();
        for (NakshaMISDataEntrySolrAndVender v : vendors) {
            int counted = 0;
            int done = 0;
            int started = 0;
            for (Integer s : Arrays.asList(v.getTechStatus(), v.getTech2ObliqueStatus(), v.getTech3ObliqueStatus())) {
                if (s == null || s == CompletionStatus.NOT_AVAILABLE.getCode()) {
                    continue;
                }
                counted++;
                if (DONE.contains(s)) {
                    done++;
                } else if (s != CompletionStatus.NOT_STARTED.getCode()) {
                    started++;
                }
            }
            if (counted == 0) {
                list.add(null);
            } else if (done == counted) {
                list.add(CompletionStatus.COMPLETED.getCode());
            } else if (done + started > 0) {
                list.add(CompletionStatus.UNDER_PROCESS.getCode());
            } else {
                list.add(CompletionStatus.NOT_STARTED.getCode());
            }
        }
        return list;
    }

    /** "Completed" when every reporting ULB is done, otherwise the split across ULBs; blank when nothing is reported. */
    private static String activityStatus(List<Integer> statuses, String doneWord) {
        int done = 0;
        int progress = 0;
        int notStarted = 0;
        for (Integer s : statuses) {
            if (s == null || s == CompletionStatus.NOT_AVAILABLE.getCode()) {
                continue;
            }
            if (DONE.contains(s)) {
                done++;
            } else if (s == CompletionStatus.NOT_STARTED.getCode()) {
                notStarted++;
            } else {
                progress++;
            }
        }
        int total = done + progress + notStarted;
        if (total == 0) {
            return "";
        }
        if (done == total) {
            return doneWord;
        }
        List<String> parts = new ArrayList<>();
        if (done > 0) {
            parts.add(doneWord + " in " + done + " of " + total + " ULBs");
        }
        if (progress > 0) {
            parts.add((parts.isEmpty() ? "Under progress in " + progress + " of " + total : "under progress in " + progress) + " ULBs");
        }
        if (notStarted > 0) {
            parts.add((parts.isEmpty() ? "Not started in " + notStarted + " of " + total : "not started in " + notStarted) + " ULBs");
        }
        return String.join(", ", parts);
    }

    private String roversTeamsText(long stateId) {
        NakshaMISDataEntry entry = null;
        try {
            for (NakshaMISDataEntry e : nakshaMISDataRepository.findAll()) {
                if (e.getState() != null && e.getState().getId() != null && e.getState().getId() == stateId) {
                    entry = e;
                    break;
                }
            }
        } catch (RuntimeException e) {
            logger.warn("Could not read NAKSHA state entry for state {}", stateId, e);
        }
        if (entry == null) {
            return "";
        }
        List<String> parts = new ArrayList<>();
        if (entry.getRoversProcuredForFieldSurvey() != null) {
            parts.add(entry.getRoversProcuredForFieldSurvey() + " Rovers procured"
                    + (entry.getRoversSanctioned() == null ? "" : " (out of " + entry.getRoversSanctioned() + " sanctioned)"));
        }
        if (entry.getTotalProfessionalsRecruited() != null) {
            parts.add(entry.getTotalProfessionalsRecruited() + " SPMU professionals recruited"
                    + (entry.getTotalSPMUPositionsSanctioned() == null ? "" : " (out of " + entry.getTotalSPMUPositionsSanctioned() + " positions)"));
        }
        if (entry.getTeamsFormedForFieldSurvey() != null) {
            parts.add(entry.getTeamsFormedForFieldSurvey() + " field survey Teams formed"
                    + (entry.getTeamsFormedForsanctioned() == null ? "" : " (out of " + entry.getTeamsFormedForsanctioned() + " sanctioned)"));
        }
        return String.join(", ", parts);
    }

    private static String groundTruthingText(List<String[]> ulbs) {
        List<String> parts = new ArrayList<>();
        for (String[] u : ulbs) {
            if (!isBlank(u[1])) {
                parts.add(u[0] + " (" + u[1] + ")");
            }
        }
        return parts.isEmpty() ? "" : "GT: " + String.join(", ", parts) + ".";
    }

    private String capacityBuildingText(long stateId) {
        int masterTrainers = 0;
        int trained = 0;
        int toBeTrained = 0;
        boolean any = false;
        try {
            for (CapacityBuilding c : capacityBuildingRepository.findAll()) {
                if (c.getState() == null || c.getState().getId() == null || c.getState().getId() != stateId) {
                    continue;
                }
                any = true;
                masterTrainers += c.getTotalMasterTrainers() == null ? 0 : c.getTotalMasterTrainers();
                trained += c.getMembersToBeTrained() == null ? 0 : c.getMembersToBeTrained();
                toBeTrained += c.getMembersTrained() == null ? 0 : c.getMembersTrained();
            }
        } catch (RuntimeException e) {
            logger.warn("Could not read NAKSHA capacity building for state {}", stateId, e);
        }
        if (!any) {
            return "";
        }
        String pct = toBeTrained > 0
                ? " (" + BigDecimal.valueOf(trained * 100.0 / toBeTrained).setScale(1, RoundingMode.HALF_UP).toPlainString() + "%)"
                : "";
        return masterTrainers + " Master Trainers and " + trained + " Field Officers" + pct + " Trained";
    }

    /**
     * NAKSHA row of the DILRMP component expenditure status: {total sanctioned, financial year, released in that year}.
     * The year is the one containing the report date, or the latest earlier year with a release.
     */
    private String[] nakshaRelease(long stateId, LocalDate asOnDate) {
        String[] result = {"", "", ""};
        List<DilrmpComponentExpenditureStatus> rows = new ArrayList<>();
        try {
            for (DilrmpComponentExpenditureStatus r : dilrmpComponentExpenditureStatusRepository.findAll()) {
                if (r.getStateId() != null && r.getStateId() == stateId
                        && r.getSerialNo() != null && r.getSerialNo() == NAKSHA_EXPENDITURE_SERIAL_NO) {
                    rows.add(r);
                }
            }
        } catch (RuntimeException e) {
            logger.warn("Could not read NAKSHA expenditure status for state {}", stateId, e);
        }
        if (rows.isEmpty()) {
            return result;
        }
        BigDecimal sanctioned = null;
        for (DilrmpComponentExpenditureStatus r : rows) {
            if (r.getSanctionIssued() != null || r.getSanctionCancelled() != null) {
                sanctioned = (sanctioned == null ? BigDecimal.ZERO : sanctioned)
                        .add(r.getSanctionIssued() == null ? BigDecimal.ZERO : r.getSanctionIssued())
                        .subtract(r.getSanctionCancelled() == null ? BigDecimal.ZERO : r.getSanctionCancelled());
            }
        }
        result[0] = croreText(sanctioned);

        int startYear = asOnDate.getMonthValue() >= 4 ? asOnDate.getYear() : asOnDate.getYear() - 1;
        String currentFy = startYear + "-" + String.format("%02d", (startYear + 1) % 100);
        String year = null;
        for (DilrmpComponentExpenditureStatus r : rows) {
            String fy = r.getFinancialYear();
            if (fy != null && r.getFundReleased() != null && r.getFundReleased().signum() > 0
                    && fy.compareTo(currentFy) <= 0 && (year == null || fy.compareTo(year) > 0)) {
                year = fy;
            }
        }
        if (year == null) {
            return result;
        }
        BigDecimal released = BigDecimal.ZERO;
        for (DilrmpComponentExpenditureStatus r : rows) {
            if (year.equals(r.getFinancialYear()) && r.getFundReleased() != null) {
                released = released.add(r.getFundReleased());
            }
        }
        result[1] = year;
        result[2] = croreText(released);
        return result;
    }

    /** ULB name and ground-truthing % for the state; ULBs without a ground-truthing entry get a blank %. */
    private List<String[]> nakshaUlbs(long stateId, String stateName, List<ULBMaster> ulbMasters) {
        Map<String, String> ulbs = new LinkedHashMap<>();
        for (ULBMaster ulb : ulbMasters) {
            if (!isBlank(ulb.getUlb_name())) {
                ulbs.put(ulb.getUlb_name().trim(), "");
            }
        }
        try {
            String key = normalise(stateName);
            for (Map<String, Object> row : groundTruthingDetailService.getGroundTruthingReportData()) {
                if (!key.equals(normalise(String.valueOf(row.get("stateName"))))) {
                    continue;
                }
                Object ulb = row.get("ulbName");
                if (ulb == null || isBlank(ulb.toString())) {
                    continue;
                }
                Object pct = "Area".equalsIgnoreCase(String.valueOf(row.get("surveyType")))
                        ? row.get("areaWiseSurveyCompletionPercent") : row.get("propertyWiseSurveyCompletionPercent");
                if (pct == null) {
                    pct = row.get("surveyUnitWiseCompletionPercent");
                }
                ulbs.put(ulb.toString().trim(), percentText(pct));
            }
        } catch (RuntimeException e) {
            logger.warn("Could not read NAKSHA ground truthing for state {}", stateId, e);
        }
        List<String[]> list = new ArrayList<>();
        for (Map.Entry<String, String> e : ulbs.entrySet()) {
            list.add(new String[]{e.getKey(), e.getValue()});
        }
        return list;
    }

    private static String defaultUlbText(String state, List<String[]> ulbs) {
        if (ulbs.isEmpty()) {
            return "";
        }
        List<String> names = new ArrayList<>();
        for (String[] u : ulbs) {
            names.add(u[0]);
        }
        return "In " + state + ", " + ulbs.size() + " Urban Local Bodies (ULBs) namely " + String.join(", ", names)
                + ", have been included under NAKSHA for creation of Land records in urban areas.";
    }

    private static String percentText(Object value) {
        if (value == null || isBlank(value.toString())) {
            return "";
        }
        try {
            BigDecimal pct = new BigDecimal(value.toString().replace("%", "").trim()).setScale(2, RoundingMode.HALF_UP);
            return pct.stripTrailingZeros().toPlainString() + "%";
        } catch (NumberFormatException e) {
            return value.toString();
        }
    }

    // ---------------------------------------------------------------- tables

    private static void fundTable(XWPFDocument doc, StateProfileWordInput in) {
        XWPFTable table = table(doc, 3, 5);
        String[] header = {"", "2008-09 to 2013-14", "2014-15 to 2020-21", "2021-22 to 2025-26", "Total"};
        for (int c = 0; c < header.length; c++) {
            cell(table.getRow(0).getCell(c), header[c], true, ParagraphAlignment.CENTER, true);
        }
        String[] released = {in.getFundReleased1(), in.getFundReleased2(), in.getFundReleased3()};
        String[] spent = {in.getFundSpent1(), in.getFundSpent2(), in.getFundSpent3()};
        cell(table.getRow(1).getCell(0), "Fund Released", true, ParagraphAlignment.LEFT, false);
        cell(table.getRow(2).getCell(0), "Fund Spent", true, ParagraphAlignment.LEFT, false);
        for (int i = 0; i < 3; i++) {
            cell(table.getRow(1).getCell(i + 1), value(released[i]), false, ParagraphAlignment.CENTER, false);
            cell(table.getRow(2).getCell(i + 1), value(spent[i]), false, ParagraphAlignment.CENTER, false);
        }
        cell(table.getRow(1).getCell(4), sum(released), true, ParagraphAlignment.CENTER, false);
        cell(table.getRow(2).getCell(4), sum(spent), true, ParagraphAlignment.CENTER, false);
        spacer(doc);
    }

    private static void physicalTable(XWPFDocument doc, String state, List<ComponentRow> rows, List<String> reasons) {
        XWPFTable table = table(doc, rows.size() + 2, 8);
        String[] top = {"Sl. No.", "Components", "Unit", "National", "", state, "", "Reason for incompletion"};
        String[] sub = {"", "", "", "Total", "Completed", "Total", "Completed", ""};
        for (int c = 0; c < top.length; c++) {
            cell(table.getRow(0).getCell(c), top[c], true, ParagraphAlignment.CENTER, true);
            cell(table.getRow(1).getCell(c), sub[c], true, ParagraphAlignment.CENTER, true);
        }
        for (int c : new int[]{0, 1, 2, 7}) {
            vMerge(table.getRow(0).getCell(c), true);
            vMerge(table.getRow(1).getCell(c), false);
        }
        for (int c : new int[]{3, 5}) {
            hMerge(table.getRow(0).getCell(c), true);
            hMerge(table.getRow(0).getCell(c + 1), false);
        }
        for (int i = 0; i < rows.size(); i++) {
            ComponentRow row = rows.get(i);
            int r = i + 2;
            cell(table.getRow(r).getCell(0), String.valueOf(i + 1), false, ParagraphAlignment.CENTER, false);
            cell(table.getRow(r).getCell(1), row.label, false, ParagraphAlignment.LEFT, false);
            cell(table.getRow(r).getCell(2), row.unit, false, ParagraphAlignment.LEFT, false);
            cell(table.getRow(r).getCell(3), row.getNationalTotal(), false, ParagraphAlignment.CENTER, false);
            cell(table.getRow(r).getCell(4), row.getNationalCompleted(), false, ParagraphAlignment.CENTER, false);
            cell(table.getRow(r).getCell(5), row.getStateTotal(), false, ParagraphAlignment.CENTER, false);
            cell(table.getRow(r).getCell(6), row.getStateCompleted(), false, ParagraphAlignment.CENTER, false);
            cell(table.getRow(r).getCell(7), reasons != null && i < reasons.size() ? value(reasons.get(i)) : "",
                    false, ParagraphAlignment.LEFT, false);
        }
        spacer(doc);
    }

    /** Always printed; one empty row is left when no component is lagging so it can be filled in Word. */
    private static void laggingTable(XWPFDocument doc, List<ComponentRow> rows) {
        int count = Math.max(rows.size(), 1) + 1;
        XWPFTable table = table(doc, count, 3);
        String[] header = {"S.No.", "Component", "State Completed"};
        for (int c = 0; c < header.length; c++) {
            cell(table.getRow(0).getCell(c), header[c], true, ParagraphAlignment.CENTER, true);
        }
        for (int i = 1; i < count; i++) {
            ComponentRow row = i - 1 < rows.size() ? rows.get(i - 1) : null;
            cell(table.getRow(i).getCell(0), row == null ? "" : String.valueOf(i), false, ParagraphAlignment.CENTER, false);
            cell(table.getRow(i).getCell(1), row == null ? "" : row.label, false, ParagraphAlignment.LEFT, false);
            cell(table.getRow(i).getCell(2), row == null ? "" : row.state.doneText(), false, ParagraphAlignment.CENTER, false);
        }
        spacer(doc);
    }

    private static void nakshaTable(XWPFDocument doc, List<String[]> ulbs, StateProfileWordInput in) {
        Map<String, String[]> entered = in.ulbDetailsByName();
        int rows = Math.max(ulbs.size(), 1) + 1;
        XWPFTable table = table(doc, rows, 6);
        String[] header = {"S.No.", "ULB / Town / City", "GT %", "RoR %", "Timeline for Draft UrPro Card", "Final UrPro Card Publication"};
        for (int c = 0; c < header.length; c++) {
            cell(table.getRow(0).getCell(c), header[c], true, ParagraphAlignment.CENTER, true);
        }
        for (int i = 1; i < rows; i++) {
            String[] ulb = i - 1 < ulbs.size() ? ulbs.get(i - 1) : new String[]{"", ""};
            cell(table.getRow(i).getCell(0), ulbs.isEmpty() ? "" : String.valueOf(i), false, ParagraphAlignment.CENTER, false);
            cell(table.getRow(i).getCell(1), ulb[0], false, ParagraphAlignment.LEFT, false);
            cell(table.getRow(i).getCell(2), ulb[1], false, ParagraphAlignment.CENTER, false);
            String[] details = entered.get(ulb[0]);
            for (int c = 3; c < 6; c++) {
                cell(table.getRow(i).getCell(c), details == null ? "" : value(details[c - 3]), false, ParagraphAlignment.CENTER, false);
            }
        }
        spacer(doc);
    }

    // ---------------------------------------------------------------- Word helpers

    private static XWPFTable table(XWPFDocument doc, int rows, int cols) {
        XWPFTable table = doc.createTable(rows, cols);
        table.setWidth("100%");
        return table;
    }

    private static void cell(XWPFTableCell cell, String text, boolean bold, ParagraphAlignment align, boolean header) {
        XWPFParagraph p = cell.getParagraphs().get(0);
        p.setAlignment(align);
        p.setSpacingAfter(0);
        XWPFRun run = p.createRun();
        run.setFontFamily(FONT);
        run.setFontSize(9);
        run.setBold(bold);
        run.setText(text == null ? "" : text);
        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        if (header) {
            cell.setColor(HEADER_FILL);
        }
    }

    private static CTTcPr tcPr(XWPFTableCell cell) {
        CTTcPr pr = cell.getCTTc().getTcPr();
        return pr != null ? pr : cell.getCTTc().addNewTcPr();
    }

    private static void vMerge(XWPFTableCell cell, boolean first) {
        tcPr(cell).addNewVMerge().setVal(first ? STMerge.RESTART : STMerge.CONTINUE);
    }

    private static void hMerge(XWPFTableCell cell, boolean first) {
        tcPr(cell).addNewHMerge().setVal(first ? STMerge.RESTART : STMerge.CONTINUE);
    }

    private static void text(XWPFDocument doc, String text, boolean bold, int size, ParagraphAlignment align) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(align);
        XWPFRun run = p.createRun();
        run.setFontFamily(FONT);
        run.setFontSize(size);
        run.setBold(bold);
        run.setText(text == null ? "" : text);
    }

    private static void heading(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(160);
        XWPFRun run = p.createRun();
        run.setFontFamily(FONT);
        run.setFontSize(12);
        run.setBold(true);
        run.setUnderline(UnderlinePatterns.SINGLE);
        run.setText(text);
    }

    private static void labelled(XWPFDocument doc, String label, String value) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun l = p.createRun();
        l.setFontFamily(FONT);
        l.setFontSize(11);
        l.setBold(true);
        l.setText(label);
        XWPFRun v = p.createRun();
        v.setFontFamily(FONT);
        v.setFontSize(11);
        v.setText(value == null ? "" : value);
    }

    /** Each line of a text area becomes its own paragraph; an empty value leaves one blank line to type into. */
    private static void multiline(XWPFDocument doc, String value) {
        if (isBlank(value)) {
            text(doc, "", false, 11, ParagraphAlignment.LEFT);
            return;
        }
        for (String line : value.split("\\r?\\n")) {
            text(doc, line, false, 11, ParagraphAlignment.BOTH);
        }
    }

    /**
     * Numbers each line of a text area from {@code start}, keeping numbering the officer already typed.
     * An empty value leaves the first number on its own line to type after.
     */
    private static void numbered(XWPFDocument doc, String value, int start) {
        if (isBlank(value)) {
            text(doc, start + ". ", false, 11, ParagraphAlignment.LEFT);
            return;
        }
        int n = start;
        for (String line : value.split("\\r?\\n")) {
            if (isBlank(line)) {
                continue;
            }
            String trimmed = line.trim();
            if (trimmed.matches("^(\\d+[.)]|\\([0-9a-zA-Z]+\\)|[a-zA-Z][.)]).*")) {
                text(doc, trimmed, false, 11, ParagraphAlignment.BOTH);
            } else {
                text(doc, n + ". " + trimmed, false, 11, ParagraphAlignment.BOTH);
            }
            n++;
        }
    }

    private static void spacer(XWPFDocument doc) {
        doc.createParagraph().setSpacingAfter(0);
    }

    // ---------------------------------------------------------------- formatting

    private static String amount(Integer value) {
        if (value == null) {
            return "";
        }
        if (value >= ONE_CRORE) {
            return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(ONE_CRORE), 2, RoundingMode.HALF_UP) + " crore";
        }
        return NumberFormatterUtil.formatWithCommas(value);
    }

    private static String count(Integer value) {
        return value == null ? "" : NumberFormatterUtil.formatWithCommas(value);
    }

    private static String crore(String value) {
        return isBlank(value) ? "" : value.trim() + " Cr.";
    }

    private static String croreText(BigDecimal value) {
        return value == null ? "" : value.setScale(2, RoundingMode.HALF_UP).toPlainString() + " Cr.";
    }

    private static String value(String value) {
        return value == null ? "" : value.trim();
    }

    /** Sum of the entered amounts; blank when nothing is entered or any entry is not a number. */
    private static String sum(String[] values) {
        BigDecimal total = BigDecimal.ZERO;
        boolean any = false;
        for (String v : values) {
            if (isBlank(v)) {
                continue;
            }
            try {
                total = total.add(new BigDecimal(v.replace(",", "").trim()));
                any = true;
            } catch (NumberFormatException e) {
                return "";
            }
        }
        return any ? total.setScale(2, RoundingMode.HALF_UP).toPlainString() : "";
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String normalise(String name) {
        return name == null ? "" : name.toLowerCase().replaceAll("[^a-z]", "");
    }

    private static String toTitleCase(String name) {
        if (isBlank(name)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String word : name.trim().toLowerCase().split("\\s+")) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            if ("and".equals(word) || "of".equals(word)) {
                sb.append(word);
            } else {
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            }
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------- row types

    private static final class Figure {
        final Integer total;
        final Integer done;

        Figure(Integer total, Integer done) {
            this.total = total;
            this.done = done;
        }

        BigDecimal percent() {
            if (total == null || done == null || total <= 0) {
                return null;
            }
            return BigDecimal.valueOf(done).multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
        }

        String doneText() {
            if (done == null) {
                return "";
            }
            BigDecimal pct = percent();
            return amount(done) + (pct == null ? "" : " (" + pct.toPlainString() + "%)");
        }
    }

    public static final class ComponentRow {
        final String label;
        final String unit;
        final Figure national;
        final Figure state;
        String nationalDoneText;
        String stateDoneText;
        boolean excludeFromLagging;

        ComponentRow(String label, String unit, Figure national, Figure state) {
            this.label = label;
            this.unit = unit;
            this.national = national;
            this.state = state;
        }

        public String getLabel() {
            return label;
        }

        public String getUnit() {
            return unit;
        }

        public String getNationalTotal() {
            return amount(national.total);
        }

        public String getNationalCompleted() {
            return nationalDoneText != null ? nationalDoneText : national.doneText();
        }

        public String getStateTotal() {
            return amount(state.total);
        }

        public String getStateCompleted() {
            return stateDoneText != null ? stateDoneText : state.doneText();
        }
    }

    public static final class MisData {
        String stateName;
        LocalDate asOnDate;
        String districts;
        String tehsils;
        String villages;
        List<ComponentRow> rows;
        List<ComponentRow> lagging;
        List<String[]> ulbs;
        String ulbText;
        String flying;
        String roversTeams;
        String ori;
        String feature2d;
        String feature3d;
        String groundTruthing;
        String capacityBuilding;
        String sanctionedBudget;
        String releaseYear;
        String amountReleased;

        public String getStateName() { return stateName; }
        public LocalDate getAsOnDate() { return asOnDate; }
        public String getDistricts() { return districts; }
        public String getTehsils() { return tehsils; }
        public String getVillages() { return villages; }
        public List<ComponentRow> getRows() { return rows; }
        public List<ComponentRow> getLagging() { return lagging; }
        public List<String[]> getUlbs() { return ulbs; }
        public String getUlbText() { return ulbText; }
        public String getFlying() { return flying; }
        public String getRoversTeams() { return roversTeams; }
        public String getOri() { return ori; }
        public String getFeature2d() { return feature2d; }
        public String getFeature3d() { return feature3d; }
        public String getGroundTruthing() { return groundTruthing; }
        public String getCapacityBuilding() { return capacityBuilding; }
        public String getSanctionedBudget() { return sanctionedBudget; }
        public String getAmountReleased() { return amountReleased; }

        public String getReleaseLabel() {
            return "9. Amount Released" + (isBlank(releaseYear) ? "" : " in F.Y " + releaseYear);
        }
    }
}
