package in.gov.dilrmp.services.dashboard;

import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.dashboard.DashBordDTO;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.SurveyResurveyViewReport;
import in.gov.dilrmp.repositories.administrativeBoundry.StateRepositry;
import in.gov.dilrmp.repositories.physicalProgressRepositories.*;
import in.gov.dilrmp.services.administrativeBoundry.StateService;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashBoardStateService {

    @Autowired
    StateClrReportViewRepository stateClrReportViewRepository;
    @Autowired
    MapDigitizationReportRepository mapDigitizationReportRepository;
    @Autowired
    MRRViewReportRepository mrrViewReportRepository;
    @Autowired
    StateSroViewRepository stateSroViewRepository;
    @Autowired
    LinkedAadhaarReportRepository linkedAadhaarReportRepository;
    @Autowired
    StateRcmsViewRepository stateRcmsViewRepository;
    @Autowired
    SurveyResurveyviewRepository surveyResurveyviewRepository;
    @Autowired
    StateRepositry stateRepositry;
    @Autowired
    LoogedInUserUtility loogedInUserUtility;
    @Autowired
    StateService stateService;



    public List<DashBordDTO> getStateDashBoardDataByStateIds() {
        List<DashBordDTO> stateDTOList = new ArrayList<>();
        List<State> stateList = new ArrayList<>();

        if (loogedInUserUtility.hasRole("ROLE_STATE")) {
            Long loggedInUserId = loogedInUserUtility.getLoggedinUser().getUserID();

            State state = stateService.findStateByUserID(loggedInUserId);
            if (state != null) {
                stateList.add(state);
            } else {
                throw new RuntimeException("State not found for user ID: " + loggedInUserId);
            }
        } else {
            stateList = stateRepositry.statelist();
        }
        stateList.forEach(s->{
            // Fetch entity from repository based on stateId
            StateClrReportView stateCLRMoodel = stateClrReportViewRepository.findById(Long.valueOf(s.getId()))
                    .orElseThrow(() -> new RuntimeException("Data not found for ID: " + s.getId()));

            //STATE CLR the entity to DTO
            DashBordDTO stateDTO = new DashBordDTO();
            stateDTO.setStateId(Math.toIntExact(stateCLRMoodel.getStateId()));
            stateDTO.setName(stateCLRMoodel.getStateName());
            stateDTO.setTotalDistrict(stateCLRMoodel.getTotalDistrict());
            stateDTO.setTotalTehsil(stateCLRMoodel.getTotalTehsils());
            stateDTO.setTotalVillage(stateCLRMoodel.getTotalVillages());
            stateDTO.setVillagesComputerizationCompleted(stateCLRMoodel.getVillagesClrCompleted());
            stateDTO.setVillagesComputerizationCompletedPercent(stateCLRMoodel.getClrCompletionPercent());// need to set value
            stateDTO.setRorComputerized(stateCLRMoodel.getRorComputerized());
            stateDTO.setRorComputerizedPercent(stateCLRMoodel.getRorComputerizedPercent());
            stateDTO.setTotalLandOwners(stateCLRMoodel.getTotalLandOwners());
//            stateDTO.setRorBank(stateCLRMoodel.getRorBank());
//            stateDTO.setRorBankPercent(stateCLRMoodel.getRorBankPercent());


            //STATE Map  Level the entity to DTO
            MapDigitizationReport stateMapMoodel = mapDigitizationReportRepository.findById(Math.toIntExact(Long.valueOf(s.getId())))
                    .orElseThrow(() -> new RuntimeException("Data not found for ID: " + s.getId()));

            stateDTO.setTotalMapsFmbTippans(stateMapMoodel.getTotalMapsFmbTippans());
            stateDTO.setTotalDigitizedMapsFmbTippans(stateMapMoodel.getTotalDigitizedMapsFmbTippans());
            stateDTO.setDigitizedMapsFmbTippansPercent(stateMapMoodel.getDigitizedMapsFmbTippansPercent());
            stateDTO.setVillagesWithUlipn(stateMapMoodel.getVillagesWithUlipn());
            stateDTO.setVillagesWithUlipnPercent(stateMapMoodel.getVillagesWithUlipnPercent());
            stateDTO.setVillagesLinkedWithRor(stateMapMoodel.getVillagesLinkedWithRor());
            stateDTO.setVillagesLinkedWithRoRPercent(stateMapMoodel.getVillagesLinkedWithRorPercent());
            stateDTO.setVillagesGeoreferenced(stateMapMoodel.getVillagesGeoreferenced());
            stateDTO.setVillagesGeoreferencedPercent(stateMapMoodel.getVillagesGeoreferencedPercent());


            // SRO State Level the entity to DTO
            SroReportDTO stateSROModel = stateSroViewRepository.findById(Long.valueOf(s.getId()))
                    .orElseThrow(() -> new RuntimeException("Data not found for ID: " + s.getId()));

            stateDTO.setTotalSro(stateSROModel.getNumberOfSROsInState());
            stateDTO.setSroComputerized(stateSROModel.getNumberOfSROsUsingOnlineRegistration());
            stateDTO.setSroComputerizedPercent(stateSROModel.getOnlineRegistrationPercent());



            //STATE Survey Resurvey  Level the entity to DTO
            SurveyResurveyViewReport stateSurveyModel = surveyResurveyviewRepository.findById(Long.valueOf(s.getId()))
                    .orElseThrow(() -> new RuntimeException("Data not found for ID: " + s.getId()));

            stateDTO.setVillagesMap1Generated(stateSurveyModel.getVillagesMap1Generated());
            stateDTO.setVillagesDroneFlyingCompleted(stateSurveyModel.getVillagesFinalPromulgationDone());
            stateDTO.setVillagesDroneFlyingCompleted(stateSurveyModel.getVillagesDroneFlyingCompleted());
            stateDTO.setVillagesDroneFlyingCompletedPercent(stateSurveyModel.getSurveyCompletedVillagePercent());


            //STATE RCMS  Level the entity to DTO
            RcmsReportDTO stateRCMSModel = stateRcmsViewRepository.findById(Long.valueOf(s.getId()))
                    .orElseThrow(() -> new RuntimeException("Data not found for ID: " + s.getId()));

            stateDTO.setTotalRevenueCourts(stateRCMSModel.getTotalRevenueCourts());
            stateDTO.setRevenueCourtsComputerized(stateRCMSModel.getRevenueCourtsComputerized());
            stateDTO.setTotalRevenueCourtsPercent(BigDecimal.valueOf(stateRCMSModel.getRevenueCourtsComputerizedPercent()));// need to set
            stateDTO.setLandRecordIntegration(0);// need to set
            stateDTO.setLandRecordIntegrationPercent(BigDecimal.valueOf(0.0));// need to set
            stateDTO.setSroIntegration(0);// need to set
            stateDTO.setSroIntegrationPercent(BigDecimal.valueOf(0.0));// need to set



            //STATE Aadhar Level the entity to DTO
            LinkedAadharViewReport stateAadharModel = linkedAadhaarReportRepository.findById(Long.valueOf(s.getId()))
                    .orElseThrow(() -> new RuntimeException("Data not found for ID: " +s.getId()));

            stateDTO.setVillagesWith100PercentRorLinkedAadhaar(stateAadharModel.getVillagesWith100PercentRorLinkedAadhaar());
            stateDTO.setVillagesWith100PercentRorLinkedAadhaarPercent(stateAadharModel.getVillagesWith100PercentRorLinkedAadhaarPercent());
            stateDTO.setRorLinkedWithAadhaar(stateAadharModel.getRorLinkedWithAadhaar());
            stateDTO.setRorLinkedWithAadhaarPercent(stateAadharModel.getRorLinkedWithAadhaarPercent());
            stateDTO.setRorLinkedWithMobileNumber(stateAadharModel.getRorLinkedWithMobileNumber());
            stateDTO.setRorLinkedWithMobileNumberPercent(stateAadharModel.getRorLinkedWithMobileNumberPercent());


            //STATE MRR Level the entity to DTO
            MrrViewReport stateMRRModel = mrrViewReportRepository.findById(Long.valueOf(s.getId()))
                    .orElseThrow(() -> new RuntimeException("Data not found for ID: " + s.getId()));

            stateDTO.setMrrCompleted(stateMRRModel.getMrrCompleted());
            stateDTO.setMrrSanctioned(stateMRRModel.getMrrSanctioned());
            stateDTO.setMrrCompletedOutOfTotalSanctionedPercent(BigDecimal.valueOf(stateMRRModel.getMrrCompletedOutOfSanctionedPercent()));
            stateDTO.setMrrCompletedOutOfTotalTehsilsPercent(stateMRRModel.getMrrCompletedOutOfTotalTehsilsPercent());



            // Add the DTO to the list
            stateDTOList.add(stateDTO);}
        );

        return stateDTOList;
    }



    public List<DashBordDTO> getTop5StatesByPercent(List<DashBordDTO> stateDTOList) {
        return stateDTOList.stream()
                .sorted(Comparator
                        .comparing(this::countFullPercentFields) // Sort by number of 100% fields
                        .reversed() // Higher count comes first
                        .thenComparing(this::getHighestPercentage, Comparator.reverseOrder()) // Highest percentage as a tie-breaker
                        .thenComparing(this::getTotalPercentages, Comparator.reverseOrder()) // Total percentages as the next tie-breaker
                )
                .limit(5)
                .collect(Collectors.toList());
    }

    public List<DashBordDTO> getBottom5StatesByPercent(List<DashBordDTO> stateDTOList) {
        return stateDTOList.stream()
                .sorted(Comparator
                        .comparing(this::countFullPercentFields) // Sort by number of 100% fields
                        .thenComparing(this::getHighestPercentage) // Highest percentage as a tie-breaker
                        .thenComparing(this::getTotalPercentages) // Total percentages as the next tie-breaker
                )
                .limit(5)
                .collect(Collectors.toList());
    }

    // Count of fields that are exactly 100%
    private int countFullPercentFields(DashBordDTO dto) {
        int count = 0;
        BigDecimal fullScore = new BigDecimal("100.0");

        if (isFullScore(dto.getVillagesComputerizationCompletedPercent(), fullScore)) count++;
        if (isFullScore(dto.getDigitizedMapsFmbTippansPercent(), fullScore)) count++;
        if (isFullScore(dto.getSroComputerizedPercent(), fullScore)) count++;
        if (isFullScore(dto.getSroLrPercent(), fullScore)) count++;
        if (isFullScore(dto.getMrrCompletedOutOfTotalSanctionedPercent(), fullScore)) count++;
        if (isFullScore(dto.getVillagesLinkedWithRoRPercent(), fullScore)) count++;

        return count;
    }
// Get the highest percentage among all fields
  private BigDecimal getHighestPercentage(DashBordDTO dto) {
      BigDecimal maxPercent = BigDecimal.ZERO;

      maxPercent = maxPercent.max(dto.getVillagesComputerizationCompletedPercent() != null ? dto.getVillagesComputerizationCompletedPercent() : BigDecimal.ZERO);
      maxPercent = maxPercent.max(dto.getDigitizedMapsFmbTippansPercent() != null ? dto.getDigitizedMapsFmbTippansPercent() : BigDecimal.ZERO);
      maxPercent = maxPercent.max(dto.getSroComputerizedPercent() != null ? dto.getSroComputerizedPercent() : BigDecimal.ZERO);
      maxPercent = maxPercent.max(dto.getSroLrPercent() != null ? dto.getSroLrPercent() : BigDecimal.ZERO);
      maxPercent = maxPercent.max(dto.getMrrCompletedOutOfTotalSanctionedPercent() != null ? dto.getMrrCompletedOutOfTotalSanctionedPercent() : BigDecimal.ZERO);
      maxPercent = maxPercent.max(dto.getVillagesLinkedWithRoRPercent() != null ? dto.getVillagesLinkedWithRoRPercent() : BigDecimal.ZERO);

      return maxPercent;
  }

    // Method to get the total sum of percentages
    private BigDecimal getTotalPercentages(DashBordDTO dto) {
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(dto.getVillagesComputerizationCompletedPercent() != null ? dto.getVillagesComputerizationCompletedPercent() : BigDecimal.ZERO);
        total = total.add(dto.getDigitizedMapsFmbTippansPercent() != null ? dto.getDigitizedMapsFmbTippansPercent() : BigDecimal.ZERO);
        total = total.add(dto.getSroComputerizedPercent() != null ? dto.getSroComputerizedPercent() : BigDecimal.ZERO);
        total = total.add(dto.getSroLrPercent() != null ? dto.getSroLrPercent() : BigDecimal.ZERO);
        total = total.add(dto.getMrrCompletedOutOfTotalSanctionedPercent() != null ? dto.getMrrCompletedOutOfTotalSanctionedPercent() : BigDecimal.ZERO);
        total = total.add(dto.getVillagesLinkedWithRoRPercent() != null ? dto.getVillagesLinkedWithRoRPercent() : BigDecimal.ZERO);

        return total;
    }

    // Helper method to check if a percentage is exactly 100%
    private boolean isFullScore(BigDecimal value, BigDecimal fullScore) {
        return value != null && value.compareTo(fullScore) == 0;
    }


    public List<DashBordDTO> gradingOfState() {
        DecimalFormat df = new DecimalFormat("0.00");
        List<Integer> selectedparameters = new ArrayList<Integer>();
        selectedparameters.add(0);
        selectedparameters.add(1);
        selectedparameters.add(2);
        selectedparameters.add(3);
        selectedparameters.add(4);
        selectedparameters.add(8);
        String z = "", c = "", v = "", b = "", n = "", m = "", aa = "", re = "", mrr = "";
        Boolean[] flags = new Boolean[9]; //8
        Boolean[] result = new Boolean[9]; //8
        Arrays.fill(flags, Boolean.FALSE);
        Arrays.fill(result, Boolean.FALSE);
        List<DashBordDTO> rr;
        List<DashBordDTO> rr2;
        List<DashBordDTO> rr3;
        List<Integer> resultpercent;
        List<Integer> option;
        Double cl = null, mpm = null, ca = null, sr = null, srl = null;
        rr = new ArrayList<DashBordDTO>();
        rr2 = new ArrayList<DashBordDTO>();
        rr3 = new ArrayList<DashBordDTO>();
        rr = getStateDashBoardDataByStateIds();

        for (DashBordDTO list : rr) {

           /* if (list.getTotalSro() == null || list.getTotalSro().toString().equals("0")) {
                continue;
            }*/
            if (list.getTotalMapsFmbTippans()== null || list.getTotalMapsFmbTippans().toString().equals("0")) {
                continue;
            }
            if (list.getTotalVillage() == null || list.getTotalVillage().toString().equals("0")) {
                continue;
            }
//                if (list.getTotaltehsils() == null || list.getTotaltehsils().toString().equals("0")) {
//                    continue;
//                }
            if (list.getTotalTehsil() == null || list.getTotalTehsil().toString().equals("0")) {
                continue;
            }
            int digi;
            int srointegratd;
            int revenue;
            int clr;
            int cadastralmaps;
            int banklinkage;
            int aadhar;
            Double mrree = null;
            int p = 0, g = 0, s = 0;

            list.setPlatinum(0);
            list.setGold(0);
            list.setSilver(0);

            if (selectedparameters.contains(0)) {
                java.lang.Double digiti = (((list.getTotalDigitizedMapsFmbTippans().doubleValue()) / (list.getTotalMapsFmbTippans().doubleValue())) * 100.00);
                n = df.format(digiti);
                mpm = digiti;

                if (digiti < 90.0) {
                    n = "";
                } else {
                    n = df.format(digiti);
                }

                digi = (int) ((float) (((float) list.getTotalDigitizedMapsFmbTippans()) / ((float) list.getTotalMapsFmbTippans())) * 100);
                result[0] = ((flags[0] == true));
                if (digiti > 99.0) {
                    p++;
                    list.setPlatinum(p);
                    n = df.format(digiti);
                }
                if (digiti >= 95.0 && digiti < 99.0) {
                    g++;
                    list.setGold(g);
                    n = df.format(digiti);
                }
                if (digiti >= 90.0 && digiti < 95.0) {
                    s++;
                    list.setSilver(s);
                    n = df.format(digiti);
                }
            } else {
                result[0] = false;
            }
            if (selectedparameters.contains(3)) {
                java.lang.Double srocomputized1 = (((list.getSroComputerized().doubleValue()) / (list.getTotalSro().doubleValue())) * 100.00);
                v = df.format(srocomputized1);
                sr = srocomputized1;
                result[3] = (((flags[3] == true)));

                if (srocomputized1 < 90.0) {
                    v = "";

                } else {
                    v = df.format(srocomputized1);
                }

                if (srocomputized1 > 99.0) {
                    p++;
                    list.setPlatinum(p);
                    v = df.format(srocomputized1);
                }
                if (srocomputized1 >= 95.0 && srocomputized1 < 99.0) {
                    g++;
                    list.setGold(g);
                    v = df.format(srocomputized1);
                }
                if (srocomputized1 >= 90.0 && srocomputized1 < 95.0) {
                    s++;
                    list.setSilver(s);
                    v = df.format(srocomputized1);
                }
            } else {
                result[3] = false;
            }
            if (selectedparameters.contains(4)) {
                java.lang.Double srointegratd1 = (((list.getSroIntegration().doubleValue()) / (list.getTotalSro().doubleValue())) * 100.00);
                b = df.format(srointegratd1);
                srl = srointegratd1;
                srointegratd = (int) ((float) (((float) list.getSroIntegration()) / ((float) list.getTotalSro())) * 100);
                result[4] = ((flags[4] == true));

                if (srointegratd1 < 90.0) {
                    b = "";

                } else {
                    b = df.format(srointegratd1);
                }

                if (srointegratd1 > 99.0) {
                    p++;
                    list.setPlatinum(p);
                    b = df.format(srointegratd1);
                }
                if (srointegratd1 >= 95.0 && srointegratd1 < 99.0) {
                    g++;
                    list.setGold(g);
                    b = df.format(srointegratd1);
                }
                if (srointegratd1 >= 90.0 && srointegratd1 < 95.0) {
                    s++;
                    list.setSilver(s);
                    b = df.format(srointegratd1);
                }

            } else {
                result[4] = false;
            }
            if (selectedparameters.contains(7)) {
                java.lang.Double revenue1 = (((list.getLandRecordIntegration().doubleValue()) / (list.getTotalTehsil().doubleValue())) * 100.00);
                re = df.format(revenue1);
                revenue = (int) ((float) (((float) list.getLandRecordIntegration()) / ((float) list.getTotalTehsil())) * 100);
                result[7] = ((flags[7] == true));

                if (revenue1 < 90.0) {
                    re = "";

                } else {
                    re = df.format(revenue1);
                }

                if (revenue1 > 99.0) {
                    p++;
                    list.setPlatinum(p);
                    re = df.format(revenue1);
                }
                if (revenue1 >= 95.0 && revenue1 < 99.0) {
                    g++;
                    list.setGold(g);
                    re = df.format(revenue1);
                }
                if (revenue1 >= 90.0 && revenue1 < 95.0) {
                    s++;
                    list.setSilver(s);
                    re = df.format(revenue1);
                }
            } else {
                result[7] = false;
            }

            if (selectedparameters.contains(1)) {
                java.lang.Double clr1 = (((list.getVillagesComputerizationCompleted().doubleValue()) / (list.getTotalVillage().doubleValue())) * 100.00);
                z = df.format(clr1);
                cl = clr1;
                clr = (int) ((float) (((float) list.getVillagesComputerizationCompleted()) / ((float) list.getTotalVillage())) * 100);
                result[1] = ((flags[1] == true));

                if (clr1 < 90.0) {
                    z = "";

                } else {
                    z = df.format(clr1);
                }

                if (clr1 > 99.0) {
                    p++;
                    list.setPlatinum(p);
                    z = df.format(clr1);
                }
                if (clr1 >= 95.0 && clr1 < 99.0) {
                    g++;
                    list.setGold(g);
                    z = df.format(clr1);
                }
                if (clr1 >= 90.0 && clr1 < 95.0) {
                    s++;
                    list.setSilver(s);
                    z = df.format(clr1);
                }

            } else {
                result[1] = false;
            }
            if (selectedparameters.contains(2)) {
                java.lang.Double cadastralmaps1 = (((list.getVillagesLinkedWithRor().doubleValue()) / (list.getTotalVillage().doubleValue())) * 100.00);
                c = df.format(cadastralmaps1);
                ca = cadastralmaps1;
                cadastralmaps = (int) ((float) (((float) list.getVillagesLinkedWithRor()) / ((float) list.getTotalVillage())) * 100);
                result[2] = ((flags[2] == true));

                if (cadastralmaps1 < 90.0) {
                    c = "";

                } else {
                    c = df.format(cadastralmaps1);
                }

                if (cadastralmaps1 > 99.0) {
                    p++;
                    list.setPlatinum(p);
                    c = df.format(cadastralmaps1);
                }

                if (cadastralmaps1 >= 95.0 && cadastralmaps1 < 99.0) {
                    g++;
                    list.setGold(g);
                    c = df.format(cadastralmaps1);
                }

                if (cadastralmaps1 >= 90.0 && cadastralmaps1 < 95.0) {
                    s++;
                    list.setSilver(s);
                    c = df.format(cadastralmaps1);
                }

            } else {
                result[2] = false;
            }
            if (selectedparameters.contains(5)) {
                java.lang.Double banklinkage1 = (((list.getRorBank().doubleValue()) / (list.getTotalVillage().doubleValue())) * 100.00);
                m = df.format(banklinkage1);
                banklinkage = (int) ((float) (((float) list.getRorBank()) / ((float) list.getTotalVillage())) * 100);
                result[5] = ((flags[5] == true));

                if (banklinkage1 < 90.0) {
                    m = "";
                } else {
                    m = df.format(banklinkage1);
                }

                if (banklinkage1 > 99.0) {
                    p++;
                    list.setPlatinum(p);
                    m = df.format(banklinkage1);
                }

                if (banklinkage1 >= 95.0 && banklinkage1 < 99.0) {
                    g++;
                    list.setGold(g);
                    m = df.format(banklinkage1);
                }

                if (banklinkage1 >= 90.0 && banklinkage1 < 95.0) {
                    s++;
                    list.setSilver(s);
                    m = df.format(banklinkage1);
                }

            } else {
                result[5] = false;

            }
            if (selectedparameters.contains(6)) {
                java.lang.Double aadhar1 = (((list.getVillagesWith100PercentRorLinkedAadhaar().doubleValue()) / (list.getTotalVillage().doubleValue())) * 100.00);
                aa = df.format(aadhar1);
                aadhar = (int) ((float) (((float) list.getVillagesWith100PercentRorLinkedAadhaar()) / ((float) list.getTotalVillage())) * 100);
                result[6] = ((flags[6] == true));

                if (aadhar1 < 90.0) {
                    aa = "";
                } else {
                    aa = df.format(aadhar1);
                }

                if (aadhar1 > 99.0) {
                    p++;
                    list.setPlatinum(p);
                    aa = df.format(aadhar1);
                }

                if (aadhar1 >= 95.0 && aadhar1 < 99.0) {
                    g++;
                    list.setGold(g);
                    aa = df.format(aadhar1);
                }

                if (aadhar1 >= 90.0 && aadhar1 < 95.0) {
                    s++;
                    list.setSilver(s);
                    aa = df.format(aadhar1);
                }

            } else {
                result[6] = false;
            }
            if (selectedparameters.contains(8)) {
                java.lang.Double mrrr;
                if (list.getMrrCompleted().doubleValue() == 0) {
                    mrrr = 0.0;
                    mrr = df.format(mrrr);

                } else {
                    mrrr = (((list.getMrrCompleted().doubleValue()) / (list.getMrrSanctioned().doubleValue())) * 100.00);
                    mrr = df.format(mrrr);
                }

                result[8] = ((flags[8] == true));

                if (mrrr == null) {
                    System.out.println("NA");
                }

                if (mrrr < 90.0) {
                    mrr = "";

                } else {
                    mrr = df.format(mrrr);
                }

                if (mrrr > 99.0) {
                    p++;
                    list.setPlatinum(p);
                    mrr = df.format(mrrr);
                }

                if (mrrr >= 95.0 && mrrr < 99.0) {
                    g++;
                    list.setGold(g);
                    mrr = df.format(mrrr);
                }

                if (mrrr >= 90.0 && mrrr < 95.0) {
                    s++;
                    list.setSilver(s);
                    mrr = df.format(mrrr);
                }

            } else {
                result[8] = false;
            }

            if (cl < 90.0 && mpm < 90.0 && ca < 90.0 && sr < 90.0 && srl < 90.0 && mrree < 90.0) {
                continue;
            }

            // x = df.format(spatial1);
            // int spatial = (int) ((float) (((float) list.getSpatialdata()) / ((float) list.getTotalvillages())) * 100);
            int t = 0;
            resultpercent = new ArrayList<Integer>();
            for (Boolean a : result) {
                if (a == true) {
                    resultpercent.add(t);
                }
                t++;
            }
            int y = 0;
            option = new ArrayList<Integer>();
            for (Boolean a : flags) {
                if (a == true) {
                    option.add(y);
                }
                y++;
            }

            if (resultpercent.size() == option.size()) {
                if(n.equals("NaN")){
                    n="N/A";
                }
                if(z.equals("NaN")){
                    z="N/A";
                }
                if(v.equals("NaN")){
                    v="N/A";
                }
                if(b.equals("NaN")){
                    b="N/A";
                }
                if(mrr.equals("NaN")){
                    mrr="N/A";
                }
                if(m.equals("NaN")){
                    m="N/A";
                }
                if(aa.equals("NaN")){
                    aa="N/A";
                }
                if(re.equals("NaN")){
                    re="N/A";
                }
                list.setDigitizedmapspercent1(n);
                list.setClrpercent1(z);
                list.setCadastralmapslinkedtororpercent1(c);

                list.setSrocomputrizedpercent1(v);

                list.setSroinreragllrpercent1(b);
                list.setMrrcompletedpercent1(mrr);
                list.setBanklinkagepercent1(m);
                //list.setSpatialdata_percent1(x);
                list.setClrcompletedaadharpercent1(aa);
                list.setRevenuecourtlinkedtehsils1(re);


                rr2.add(list);
            }
        }
        Comparator<DashBordDTO> e = Comparator.comparing(DashBordDTO::getPlatinum).thenComparing(DashBordDTO::getGold).thenComparing(DashBordDTO::getGold);
        rr3 = rr2.stream().sorted(e).collect(Collectors.toList());
        Collections.reverse(rr3);

        int k = 0;
        int r = 1;
        int p1 = 0, g1 = 0, s1 = 0;
        for (DashBordDTO list : rr3) {
            int p2, g2, s2;
            if (k == 0) {
                list.setRank(1);
                p1 = list.getPlatinum();
                g1 = list.getGold();
                s1 = list.getSilver();
            } else {
                p2 = list.getPlatinum();
                g2 = list.getGold();
                s2 = list.getSilver();
                if (p1 == p2 && g1 == g2 && s1 == s2) {
                    p1 = list.getPlatinum();
                    g1 = list.getGold();
                    s1 = list.getSilver();
                    list.setRank(r);
                } else {
                    p1 = list.getPlatinum();
                    g1 = list.getGold();
                    s1 = list.getSilver();
                    list.setRank(++r);
                }
            }
            k++;
            if(p1==6){
                list.setCategory("Platinum");
            } else if (p1+g1==6) {
                list.setCategory("Gold");
            } else if(p1+g1+s1==6 && s1>0){
                list.setCategory("Silver");
            } else if (p1+g1+s1<6) {
                list.setCategory("Other");
            }
            else{
                list.setCategory("Other");
            }
        }

        Comparator<DashBordDTO> ee = Comparator.comparing(DashBordDTO::getRank).thenComparing(DashBordDTO::getName);
        rr3 = rr3.stream().sorted(ee).collect(Collectors.toList());


        return  rr3;
    }

    public Map<String, String> createLabels() {
        Map<String, String> labels = new HashMap<>();
        labels.put("serialNumber", ReportLabels.SERIAL_NUMBER);
        labels.put("stateUT", ReportLabels.STATE_UT);
        labels.put("totalDistricts", ReportLabels.TOTAL_DISTRICTS);
        labels.put("districtName", ReportLabels.DISTRICT_NAME);
        labels.put("totalTehsils", ReportLabels.TOTAL_TEHSILS);
        labels.put("total", ReportLabels.TOTAL);
        labels.put("digitized", ReportLabels.DIGITIZED);
        labels.put("totalVillages", ReportLabels.TOTAL_VILLAGES);
        labels.put("villageWhereCLRComplted", ReportLabels.VILLAGES_WHERE_CLR_COMPLETED);
        labels.put("numberOfVillages", ReportLabels.NUMBER_OF_VILLAGES);
        labels.put("clrCompleted", ReportLabels.CLR_COMPLETED);
        labels.put("noOfCadastralMapsFmBsTippans", ReportLabels.NO_OF_CADASTRAL_MAPS_FMBS_TIPPANS);
        labels.put("digitzedCadastralMapsFmBsTippans", ReportLabels.DIGITIZED_CADASTRAL_MAPS_FMBS_TIPPANS);
        labels.put("sros", ReportLabels.SROS);
        labels.put("computerizedsros", ReportLabels.COMPUTERIZED_SROS);
        labels.put("computerized", ReportLabels.COMPUTERIZED);
        labels.put("numberOfSRO", ReportLabels.NO_OF_SROS);
        labels.put("villageWhereCadatralMapLinkToROR", ReportLabels.VILLAGE_WHERE_CADASTRAL_MAPS_LINKED_TO_ROR);
        labels.put("srosLinkedWithLandRecordsRor", ReportLabels.SROS_LINKED_WITH_LAND_RECORDS_ROR);
        labels.put("sroIntegratedWithLandRecords", ReportLabels.SROS_INTEGRATED_WITH_LAND_RECORDS);
        labels.put("number", ReportLabels.NO);
        labels.put("percentage", ReportLabels.PERCENTAGE);
        labels.put("districtreportName", ReportLabels.DISTRICT_INDICATOR);
        labels.put("statereportName", ReportLabels.STATE_INDICATOR);
        labels.put("grandTotal", ReportLabels.GRAND_TOTAL);
        labels.put("nationareportName", ReportLabels.NATIONAL_PROFILE);
        labels.put("statereportName", ReportLabels.STATE_PROFILE);
        return labels;
    }
}
