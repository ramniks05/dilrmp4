package in.gov.dilrmp.services.dashboard;

import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.administrativeBoundry.District;
import in.gov.dilrmp.models.dashboard.DashBordDTO;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.DistrictMapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.DistrictClrReportView;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.DistrictLinkedAadhaarViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.DistrictMrrViewReport;
import in.gov.dilrmp.models.reportDTO.rcms.DistrictRcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.DistrictSurveyResurveyViewReport;
import in.gov.dilrmp.repositories.administrativeBoundry.AdcRepositry;
import in.gov.dilrmp.repositories.administrativeBoundry.DistrictRepositry;
import in.gov.dilrmp.repositories.administrativeBoundry.StateRepositry;
import in.gov.dilrmp.repositories.physicalProgressRepositories.*;
import in.gov.dilrmp.services.administrativeBoundry.DistrictService;
import in.gov.dilrmp.utils.LoogedInUserUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class DashBoardDistrictService {

    @Autowired
    DistrictRepositry districtRepositry;
    @Autowired
    LoogedInUserUtility loogedInUserUtility;
    @Autowired
    DistrictService districtService;

    @Autowired
    DistrictClrViewRepository districtClrViewRepository;
    @Autowired
    DistrictMapViewRepository districtMapViewRepository;
    @Autowired
    DistrictSurveyResurveyRepository districtSurveyResurveyRepository;
    @Autowired
    DistrictRcmsViewRepository districtRcmsViewRepository;
    @Autowired
    DistrictAadhaarLinkingRepository districtAadhaarLinkingRepository;
    @Autowired
    DistrictMrrViewRepository districtMrrViewRepository;

    @Autowired
    StateRepositry stateRepositry;
    @Autowired
    private AdcRepositry adcRepositry;


    public List<DashBordDTO> getDistrictDashBoardDataByLgdCode() {
        List<DashBordDTO> districtDTOList = new ArrayList<>();
        List<District> districtList = new ArrayList<>();

        if  (loogedInUserUtility.hasRole("ROLE_DISTRICT")
                || loogedInUserUtility.hasRole("ROLE_ADC")){
            Long loggedInUserId = loogedInUserUtility.getLoggedinUser().getUserID();

           if(loogedInUserUtility.hasRole("ROLE_DISTRICT")){
               District district = districtService.findDistrictByUserID(loggedInUserId);
               if (district != null) {
                   districtList.add(district);
               } else {
                   throw new RuntimeException("District not found for user ID: " + loggedInUserId);
               }
           }else{

              List<District> districts = districtService.findDistrictByAdcUserID(adcRepositry.findAdcByUserID(loggedInUserId).getId());
               if (districts != null && !districts.isEmpty()) {
                   districtList.addAll(districts);
               } else {
                   throw new RuntimeException("District not found for user ID: " + loggedInUserId);
               }
           }


        } else {
            districtList = districtRepositry.districtlist();
        }
        AtomicInteger totalDistrict= new AtomicInteger();
        districtList.forEach(s -> {
                    // Fetch entity from repository based on district lgdCode
                    DistrictClrReportView districtCLRModel = districtClrViewRepository.findById(Long.valueOf(s.getId()))
                            .orElseThrow(() -> new RuntimeException("Data not found for ID: " + s.getId()));

                    //District CLR the entity to DTO


            DashBordDTO districtDTO = new DashBordDTO();
            districtDTO.setDistrict_id(Math.toIntExact(districtCLRModel.getStateId()));
            districtDTO.setName(stateRepositry.findStateNameById(districtCLRModel.getStateId()));
            districtDTO.setDistrict_name(districtCLRModel.getDistrictName().toUpperCase());
            districtDTO.setTotalTehsil(districtCLRModel.getTotalTehsils());
            districtDTO.setTotalVillage(districtCLRModel.getTotalVillages());
            districtDTO.setVillagesComputerizationCompleted(districtCLRModel.getVillagesClrCompleted());
            districtDTO.setVillagesComputerizationCompletedPercent(districtCLRModel.getClrCompletionPercent());
            districtDTO.setRorComputerized(districtCLRModel.getRorComputerized());
            districtDTO.setRorComputerizedPercent(districtCLRModel.getRorComputerizedPercent());
            districtDTO.setTotalLandOwners(districtCLRModel.getTotalLandOwners());

            //District Map  Level the entity to DTO
            DistrictMapDigitizationReport districtDigitizationModel = districtMapViewRepository.findById(Long.valueOf(s.getId()))
                    .orElseThrow(() -> new RuntimeException("Data not found for ID: " + s.getId()));

            districtDTO.setTotalMapsFmbTippans(districtDigitizationModel.getTotalMapsFmbTippans());
            districtDTO.setTotalDigitizedMapsFmbTippans(districtDigitizationModel.getTotalDigitizedMapsFmbTippans());
            districtDTO.setDigitizedMapsFmbTippansPercent(districtDigitizationModel.getDigitizedMapsFmbTippansPercent());
            districtDTO.setVillagesWithUlipn(districtDigitizationModel.getVillagesWithUlipn());
            districtDTO.setVillagesWithUlipnPercent(districtDigitizationModel.getVillagesWithUlipnPercent());
            districtDTO.setVillagesLinkedWithRor(districtDigitizationModel.getVillagesLinkedWithRor());
            districtDTO.setVillagesLinkedWithRoRPercent(districtDigitizationModel.getVillagesLinkedWithRorPercent());
            districtDTO.setVillagesGeoreferenced(districtDigitizationModel.getVillagesGeoreferenced());
            districtDTO.setVillagesGeoreferencedPercent(districtDigitizationModel.getVillagesGeoreferencedPercent());


            //District Survey Resurvey  Level the entity to DTO
            DistrictSurveyResurveyViewReport districtSurveyModel = districtSurveyResurveyRepository.findById(Long.valueOf(s.getId()))
                    .orElseThrow(() -> new RuntimeException("Data not found for ID: " + s.getId()));

            districtDTO.setVillagesMap1Generated(Math.toIntExact(districtSurveyModel.getVillagesMap1Generated()));
//            districtDTO.setVillagesMap1GeneratedPercent(districtSurveyModel.getVillagesMap1GeneratedPercent());
//            districtDTO.setVillagesDroneFlyingCompleted(districtSurveyModel.getVillagesDroneFlyingCompleted());
            districtDTO.setSurveyCompletedVillagePercent(districtSurveyModel.getSurveyCompletedVillagePercent());

            //District RCMS  Level the entity to DTO
            DistrictRcmsReportDTO districtRCMSModel = districtRcmsViewRepository.findById(Long.valueOf(s.getId()))
                    .orElseThrow(() -> new RuntimeException("Data not found for ID: " + s.getId()));

            districtDTO.setTotalRevenueCourts(districtRCMSModel.getTotalRevenueCourts());
            districtDTO.setRevenueCourtsComputerized(districtRCMSModel.getRevenueCourtsComputerized());
            districtDTO.setTotalRevenueCourtsPercent(BigDecimal.valueOf(Double.valueOf(districtRCMSModel.getRevenueCourtsComputerizedPercent())));



            //STATE Aadhar Level the entity to DTO
            DistrictLinkedAadhaarViewReport districtAadharModel = districtAadhaarLinkingRepository.findById(Long.valueOf(s.getId()))
                    .orElseThrow(() -> new RuntimeException("Data not found for ID: " +s.getId()));

            districtDTO.setVillagesWith100PercentRorLinkedAadhaar(districtAadharModel.getVillagesWith100PercentRorLinkedAadhaar());
            districtDTO.setVillagesWith100PercentRorLinkedAadhaarPercent(BigDecimal.valueOf(Double.valueOf(districtAadharModel.getVillagesWith100PercentRorLinkedAadhaarPercent())));
            districtDTO.setRorLinkedWithAadhaar(districtAadharModel.getRorLinkedWithAadhaar());
            districtDTO.setRorLinkedWithAadhaarPercent(BigDecimal.valueOf(Double.valueOf(districtAadharModel.getRorLinkedWithAadhaarPercent())));
            districtDTO.setRorLinkedWithMobileNumber(districtAadharModel.getRorLinkedWithMobileNumber());
            districtDTO.setRorLinkedWithMobileNumberPercent(BigDecimal.valueOf(Double.valueOf(districtAadharModel.getRorLinkedWithMobileNumberPercent())));

            //District MRR Level the entity to DTO
            DistrictMrrViewReport districtMRRModel = districtMrrViewRepository.findById(Long.valueOf(s.getId()))
                    .orElseThrow(() -> new RuntimeException("Data not found for ID: " + s.getId()));

            districtDTO.setMrrCompleted(districtMRRModel.getMrrCompleted());
            districtDTO.setMrrSanctioned(districtMRRModel.getMrrSanctioned());
            districtDTO.setMrrCompletedOutOfTotalSanctionedPercent(BigDecimal.valueOf(Double.valueOf(districtMRRModel.getMrrCompletedOutOfSanctionedPercent())));
            districtDTO.setMrrCompletedOutOfTotalTehsilsPercent(BigDecimal.valueOf(Double.valueOf(districtMRRModel.getMrrCompletedOutOfTotalTehsilsPercent())));


            // Add the DTO to the list
                    districtDTOList.add(districtDTO);
                }
        );

        return districtDTOList;


    }

    public List<DashBordDTO> gradingOfDistrict() {
        DecimalFormat df = new DecimalFormat("0.00");
        List<Integer> selectedparameters = new ArrayList<Integer>();
        selectedparameters.add(0);
        selectedparameters.add(1);
        selectedparameters.add(2);
        selectedparameters.add(3);
        String z = "", c = "", n = "", m = "", aa = "", re = "", mrr = "";
        Boolean[] flags = new Boolean[9]; //8
        Boolean[] result = new Boolean[9]; //8
        Arrays.fill(flags, Boolean.FALSE);
        Arrays.fill(result, Boolean.FALSE);
        List<DashBordDTO> rr;
        List<DashBordDTO> rr2;
        List<DashBordDTO> rr3;
        List<Integer> resultpercent;
        List<Integer> option;
        Double cl = null, mpm = null, ca = null;
        rr = new ArrayList<DashBordDTO>();
        rr2 = new ArrayList<DashBordDTO>();
        rr3 = new ArrayList<DashBordDTO>();
        rr = getDistrictDashBoardDataByLgdCode();

        for (DashBordDTO list : rr) {

            if (list.getTotalMapsFmbTippans()== null || list.getTotalMapsFmbTippans().toString().equals("0")) {
                continue;
            }
            if (list.getTotalVillage() == null || list.getTotalVillage().toString().equals("0")) {
                continue;
            }
            if (list.getTotalTehsil() == null || list.getTotalTehsil().toString().equals("0")) {
                continue;
            }
            int digi;
            int clr;
            int cadastralmaps;
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
            if (selectedparameters.contains(3)) {
                java.lang.Double mrrr;
                if (list.getMrrCompleted().doubleValue() == 0) {
                    mrrr = 0.0;
                    mrr = df.format(mrrr);

                } else {
                    mrrr = (((list.getMrrCompleted().doubleValue()) / (list.getMrrSanctioned().doubleValue())) * 100.00);
                    mrr = df.format(mrrr);
                }

                result[3] = ((flags[3] == true));

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
                result[3] = false;
            }

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
                if(mrr.equals("NaN")){
                    mrr="N/A";
                }
                if(m.equals("NaN")){
                    m="N/A";
                }

                list.setDigitizedmapspercent1(n);
                list.setClrpercent1(z);
                list.setCadastralmapslinkedtororpercent1(c);
                list.setMrrcompletedpercent1(mrr);
                list.setBanklinkagepercent1(m);
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
            if(p1==4){
                list.setCategory("Platinum");
            } else if (p1+g1==4) {
                list.setCategory("Gold");
            } else if(p1+g1+s1==4 && s1>0){
                list.setCategory("Silver");
            } else if (p1+g1+s1<4) {
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