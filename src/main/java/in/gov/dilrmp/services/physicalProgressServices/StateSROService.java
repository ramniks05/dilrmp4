package in.gov.dilrmp.services.physicalProgressServices;
import in.gov.dilrmp.constants.ReportLabels;
import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.repositories.physicalProgressRepositories.StateSroViewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StateSROService {
    @Autowired
    StateSroViewRepository sroviewRepository;

    private Logger logger = LoggerFactory.getLogger(StateSROService.class);



    public Map<String, String> createLabels() {
        Map<String, String> labels = new HashMap<>();
        labels.put("serialNumber", ReportLabels.SERIAL_NUMBER);
        labels.put("stateUT", ReportLabels.STATE_UT);
        labels.put("totalState", ReportLabels.TOTAL_STATE_UT);
        labels.put("numberOfStateWhereAvailable", ReportLabels.NUMBER_OF_STATE_WHERE);
        labels.put("districtName", ReportLabels.DISTRICT_NAME);
        labels.put("totalDistricts", ReportLabels.TOTAL_DISTRICTS);
        labels.put("numberOfSROInState", ReportLabels.NUMBER_OF_SRO_IN_THE_STATE);
        labels.put("sroUsingOnlineRegistration", ReportLabels.NUMBER_SRO_USING_ONLINE_REGISTRATION);
        labels.put("yearFromLegacyRecordDigitizedSRO", ReportLabels.YEAR_FROM_LEGACY_RECORD_DIGITIZED_SRO);
        labels.put("yearLegacyRecordDigitizedPublic", ReportLabels.YEAR_LEGACY_RECORD_DIGITIZED_PUBLIC);
        labels.put("registrationSystemWithOption", ReportLabels.REGISTRATION_SYSTEM_WITH_OPTION);
        labels.put("circleRatesPublicRegistrationSystem", ReportLabels.CIRCLE_RATES_PUBLIC_REGISTRATION_SYSTEM);
        labels.put("RatesPublicRegistrationSystem", ReportLabels.RATES_PUBLIC_REGISTRATION_SYSTEM);
        labels.put("calculationAutomaticallyDoneRegistrationSystem", ReportLabels.CALCULATION_DONE_AUTOMATICALLY_REGISTRATION_SYSTEM);
        labels.put("freecalculationAutomaticallyDoneRegistrationSystem", ReportLabels.FREE_CALCULATION_DONE_AUTOMATICALLY_REGISTRATION_SYSTEM);
        labels.put("registrationSystemIntegratedEStamp", ReportLabels.REGISTRATION_SYSTEM_INTEGRATED_E_STAMP);
        labels.put("optionRefundPaidFee", ReportLabels.OPTION_AVAILABLE_REFUND_PAID_FEE);
        labels.put("optionAvailableRegistrationSystem", ReportLabels.OPTION_AVAILABLE_REGISTRATION_SYSTEM);
        labels.put("facilityOnlineVerificationDocument", ReportLabels.FACILITY_ONLINE_VERIFICATION_DOCUMENT);
        labels.put("signatureObtainedFromParties", ReportLabels.SIGNATURE_OBTAINED_FROM_PARTIES);
        labels.put("signObtainedFromParties", ReportLabels.SIGN_OBTAINED_FROM_PARTIES);
        labels.put("digitalSignatureEnabledRegistration", ReportLabels.DIGITAL_SIGNATURE_ENABLED_REGISTRATION);
        labels.put("optionEKYCRegistrationSystem", ReportLabels.OPTION_OF_EKYC_AVAILABLE_REGISTRATION_SYSTEM);
        labels.put("optionAvailableRegistrationSystemVedio", ReportLabels.OPTION_AVAILABLE_REGISTRATION_SYSTEM_VEDIO);
        labels.put("homeVisitModuleAvailableRegistration", ReportLabels.HOME_VISIT_MODULE_AVAILABLE_REGISTRATION_SYSTEM);
        labels.put("emailAutomaticallyPartiesImportant", ReportLabels.EMAIL_AUTOMATICALLY_PARTIES_IMPORTANT);
        labels.put("registeredSentAutomaticallyDigital", ReportLabels.REGISTERED_SENT_AUTOMATICALLY_DIGITAL);
        labels.put("regiSentAutomaticallyDigital", ReportLabels.REGI_SENT_AUTOMATICALLY_DIGITAL);
        labels.put("provisionOnlineGrievanceRedressal", ReportLabels.THERE_PROVISION_ONLINE_GRIEVANCE_REDRESSAL);
        labels.put("OnlineGrievanceRedressal", ReportLabels.PROVISION_ONLINE_GRIEVANCE_REDRESSAL);
        labels.put("optionAvailableRegistrationFetch", ReportLabels.OPTION_AVAILABLE_REGISTRATION_SYSTEM_FETCH);
        labels.put("registrationSystemAutoTrigger", ReportLabels.REGISTRATION_SYSTEM_FACILITY_AUTO_TRIGGER);
        labels.put("regisSystemAutoTrigger", ReportLabels.REGIS_SYSTEM_FACILITY_AUTO_TRIGGER);
        labels.put("isPendencnyOfRevenueCourt", ReportLabels.IS_PENDENCY_OF_REVENUE_COURT_CASE);
        labels.put("pendencyRevenueCourtCase", ReportLabels.PENDENCY_OF_REVENUE_COURT_CASE);
        labels.put("aadhaarPanFingerprintMasked", ReportLabels.AADHAAR_PAN_FINGERPRINT_DETAILS_MASKED);
        labels.put("aadhaPanFingerprintMasked", ReportLabels.AADHA_PAN_FINGERPRINT_DETAILS_MASKED);
        labels.put("facilityStoreRegisteredDocument", ReportLabels.FACILITY_TO_STORE_REGISTERED_DOCUMENT);
        labels.put("facilStoreRegisteredDocument", ReportLabels.FACIL_TO_STORE_REGISTERED_DOCUMENT);
        //old filed name
        labels.put("total", ReportLabels.TOTAL);
        labels.put("sros", ReportLabels.SROS);
        labels.put("computerized", ReportLabels.COMPUTERIZED);
        labels.put("srosLinkedWithLandRecordsRor", ReportLabels.SROS_LINKED_WITH_LAND_RECORDS_ROR);
        labels.put("srosIntegratedWithTehsils", ReportLabels.SROS_INTEGRATED_WITH_TEHSILS);
        labels.put("srosLegacyRecordsScannedDigitallyAvailable", ReportLabels.SROS_LEGACY_RECORDS_SCANNED_DIGITALLY_AVAILABLE);
        labels.put("srosLegacyRecordsScannedAccessibleSearchable", ReportLabels.SROS_LEGACY_RECORDS_SCANNED_ACCESSIBLE_SEARCHABLE);
        labels.put("number", ReportLabels.NO);
        labels.put("yesNo", ReportLabels.YES_NO);
        labels.put("percentage", ReportLabels.PERCENTAGE);
        labels.put("reportName", ReportLabels.SRO_REPORT);
        labels.put("grandTotal", ReportLabels.GRAND_TOTAL);
        return labels;
    }


    public List<SroReportDTO> getSroStateViwe(){

        List<SroReportDTO> sroList = sroviewRepository.findAll(Sort.by(Sort.Direction.ASC, "stateName"));
        sroList = sroList.stream()
                .filter(sro -> !sro.getLgdCode().equals(999))
                .collect(Collectors.toList());
        return sroList;
    }

   public List<SroReportDTO> getStateSroReportsGrandToatal(){

        return sroviewRepository.findAllSroByStateId(999);
    }

    public List<SroReportDTO> getSroStateDataByStateId(Integer stateId) {

        return sroviewRepository.findAllSroByStateId(stateId);
    }
}
