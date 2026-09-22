package in.gov.dilrmp.Scheduler.services;

import in.gov.dilrmp.Scheduler.models.*;
import in.gov.dilrmp.Scheduler.models.clr.DistrictClrReportViewBackup;
import in.gov.dilrmp.Scheduler.models.linkedaadhaar.DistrictLinkedAadhaarViewReportBackup;
import in.gov.dilrmp.Scheduler.repositories.*;
import in.gov.dilrmp.Scheduler.repositories.clr.DistrictClrReportViewBackupRepository;
import in.gov.dilrmp.models.dataEntryModel.DistrictMISDataEntry;
import in.gov.dilrmp.models.dataEntryModel.StateMISDataEntry;
import in.gov.dilrmp.models.dataEntryModel.StateRegistrationSystem;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.DistrictMapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.DistrictClrReportView;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.DistrictLinkedAadhaarViewReport;
import in.gov.dilrmp.models.reportDTO.linkedaadhaar.LinkedAadharViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.DistrictMrrViewReport;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.models.reportDTO.rcms.DistrictRcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.rcms.RcmsReportDTO;
import in.gov.dilrmp.models.reportDTO.sro.SroReportDTO;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.DistrictSurveyResurveyViewReport;
import in.gov.dilrmp.models.reportDTO.surveyresurvey.SurveyResurveyViewReport;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.DistrictMISDataEntryRepository;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.IgrMISDataEntryRepository;
import in.gov.dilrmp.repositories.DistrictMISDataEntryForm.StateMISDataEntryRepository;
import in.gov.dilrmp.repositories.physicalProgressRepositories.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Service
public class SchedulerServices {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerServices.class);

    @Autowired
    StateClrReportViewBackupRepository stateClrReportViewBackupRepository;
    @Autowired
    StateClrReportViewRepository stateClrReportViewRepository;
    @Autowired
    LinkedAadhaarViewReportBackupRepository linkedAadharViewReportBackupRepositry;
    @Autowired
    LinkedAadhaarReportRepository linkedAadhaarReportRepository;
    @Autowired
    MapDigitizationReportRepository mapDigitizationReportRepository;
    @Autowired
    MapDigitizationReportBackupRepository mapDigitizationReportBackupRepositry;
    @Autowired
    MrrViewReportBackupRepository mrrViewReportBackupRepositry;
    @Autowired
    MRRViewReportRepository mrrViewReportRepository;
    @Autowired
    RemsReportViewReportBackupRepository rcmsReportViewReportBackupRepositry;
    @Autowired
    StateRcmsViewRepository stateRcmsViewRepository;
    @Autowired
    StateSroViewReportBackupRepository stateSroViewReportBackupRepositry;
    @Autowired
    StateSroViewRepository stateSroViewRepository;
    @Autowired
    SurveyResurveyViewReportBackupRepository surveyResurveyViewReportBackupRepository;
    @Autowired
    SurveyResurveyviewRepository surveyResurveyviewRepository;

    @Autowired
    DistrictClrViewRepository districtClrViewRepository;
    @Autowired
    DistrictClrReportViewBackupRepository districtClrReportViewBackupRepository;
    @Autowired
    DistrictAadhaarLinkingRepository districtAadhaarLinkingRepository;
    @Autowired
    DistrictLinkedAadhaarViewReportBackupRepository districtLinkedAadhaarViewReportBackupRepository;

    @Autowired
    DistrictMrrViewRepository districtMrrViewRepository;
    @Autowired
    DistrictMrrViewReportBackupRepository districtMrrViewReportBackupRepository;

    @Autowired
    DistrictRcmsViewRepository districtRcmsReportBackup;

    @Autowired
    DistrictRcmsReportBackupRepository districtRcmsReportBackupRepository;

    @Autowired
    DistrictSurveyResurveyRepository districtSurveyResurveyRepository;
    @Autowired
    DistrictSurveyResurveyViewReportBackupRepository districtSurveyResurveyViewReportBackupRepository;

    @Autowired
    DistrictMapViewRepository districtMapViewRepository;
    @Autowired
    DistrictMapDigitizationReportBackupRepository districtMapDigitizationReportBackupRepository;

    @Autowired
    private DistrictMISDataEntryRepository districtMISDataEntryRepository;

    @Autowired
    private DistrictMISDataEntryBackupRepository misDataEntryBackupRepository;
    @Autowired
    StateMISDataEntryRepository stateMISDataEntryRepository;
    @Autowired
    StateMISDataEntryBackupRepository stateMISDataEntryBackupRepository;
    @Autowired
    IgrMISDataEntryRepository igrMISDataEntryRepository;
    @Autowired
    StateRegistrationSystemBackupRepository stateRegistrationSystemBackupRepository;





    public void backupStateClrReportViewData() {
        try {
            List<StateClrReportView> stateClrReports = stateClrReportViewRepository.findAll();
            for (StateClrReportView stateReport : stateClrReports) {
                StateClrReportViewBackup backup = new StateClrReportViewBackup();
                BeanUtils.copyProperties(stateReport, backup);
                backup.setBackupDate(LocalDate.now());
                stateClrReportViewBackupRepository.save(backup);
            }
            logger.info("Successfully backed up State CLR Report View data.");
        } catch (Exception e) {
            logger.error("Error occurred while backing up State CLR Report View data: ", e);
        }
    }

    public void backupLinkedAadharViewData() {
        try {
            List<LinkedAadharViewReport> reports = linkedAadhaarReportRepository.findAll();
            for (LinkedAadharViewReport report : reports) {
                LinkedAadharViewReportBackup backup = new LinkedAadharViewReportBackup();
                BeanUtils.copyProperties(report, backup);
                backup.setBackupDate(LocalDate.now());
                linkedAadharViewReportBackupRepositry.save(backup);
            }
            logger.info("Successfully backed up Linked Aadhaar View data.");
        } catch (Exception e) {
            logger.error("Error occurred while backing up Linked Aadhaar View data: ", e);
        }
    }

    public void backupMapDigitizationViewData() {
        try {
            List<MapDigitizationReport> reports = mapDigitizationReportRepository.findAll();
            for (MapDigitizationReport report : reports) {
                MapDigitizationReportBackup backup = new MapDigitizationReportBackup();
                BeanUtils.copyProperties(report, backup);
                backup.setBackupDate(LocalDate.now());
                mapDigitizationReportBackupRepositry.save(backup);
            }
            logger.info("Successfully backed up Map Digitization data.");
        } catch (Exception e) {
            logger.error("Error occurred while backing up Map Digitization data: ", e);
        }
    }

    public void backupMrrViewData() {
        try {
            List<MrrViewReport> reports = mrrViewReportRepository.findAll();
            for (MrrViewReport report : reports) {
                MrrViewReportBackup backup = new MrrViewReportBackup();
                BeanUtils.copyProperties(report, backup);
                backup.setBackupDate(LocalDate.now());
                mrrViewReportBackupRepositry.save(backup);
            }
            logger.info("Successfully backed up MRR View data.");
        } catch (Exception e) {
            logger.error("Error occurred while backing up MRR View data: ", e);
        }
    }

    public void backupStateRcmsViewData() {
        try {
            List<RcmsReportDTO> reports = stateRcmsViewRepository.findAll();
            for (RcmsReportDTO report : reports) {
                RcmsReportViewReportBackup backup = new RcmsReportViewReportBackup();
                BeanUtils.copyProperties(report, backup);
                backup.setBackupDate(LocalDate.now());
                rcmsReportViewReportBackupRepositry.save(backup);
            }
            logger.info("Successfully backed up State RCMS View data.");
        } catch (Exception e) {
            logger.error("Error occurred while backing up State RCMS View data: ", e);
        }
    }

    public void backupStateSorViewData() {
        try {
            List<SroReportDTO> reports = stateSroViewRepository.findAll();
            for (SroReportDTO report : reports) {
                SroViewReportBackup backup = new SroViewReportBackup();
                BeanUtils.copyProperties(report, backup);
                backup.setBackupDate(LocalDate.now());
                stateSroViewReportBackupRepositry.save(backup);
            }
            logger.info("Successfully backed up State SRO View data.");
        } catch (Exception e) {
            logger.error("Error occurred while backing up State SRO View data: ", e);
        }
    }

    public void backupStateSurveyResurveyViewData() {
        try {
            List<SurveyResurveyViewReport> reports = surveyResurveyviewRepository.findAll();
            for (SurveyResurveyViewReport report : reports) {
                SurveyResurveyViewReportBackup backup = new SurveyResurveyViewReportBackup();
                BeanUtils.copyProperties(report, backup);
                backup.setBackupDate(LocalDate.now());
                surveyResurveyViewReportBackupRepository.save(backup);
            }
            logger.info("Successfully backed up State Survey/Resurvey View data.");
        } catch (Exception e) {
            logger.error("Error occurred while backing up State Survey/Resurvey View data: ", e);
        }
    }

    public void backupDistrictClrReportViewData() {
        try {
            List<DistrictClrReportView> reports = districtClrViewRepository.findAll();
            for (DistrictClrReportView report : reports) {
                DistrictClrReportViewBackup backup = new DistrictClrReportViewBackup();
                BeanUtils.copyProperties(report, backup);
                backup.setBackupDate(LocalDate.now());
                districtClrReportViewBackupRepository.save(backup);
            }
            logger.info("Successfully backed up District Clr Report View data.");
        } catch (Exception e) {
            logger.error("Error occurred while backing up District Clr Report View data: ", e);
        }
    }

    public void backupDistrictLinkedAadhaarViewData() {
        try {
            List<DistrictLinkedAadhaarViewReport> reports = districtAadhaarLinkingRepository.findAll();
            for (DistrictLinkedAadhaarViewReport report : reports) {
                DistrictLinkedAadhaarViewReportBackup backup = new DistrictLinkedAadhaarViewReportBackup();
                BeanUtils.copyProperties(report, backup);
                backup.setBackupDate(LocalDate.now());
                districtLinkedAadhaarViewReportBackupRepository.save(backup);
            }
            logger.info("Successfully backed up District Linked Aadhaar Report View data.");
        } catch (Exception e) {
            logger.error("Error occurred while backing up District Linked Aadhaar View data: ", e);
        }
    }

    public void backupDistrictMrrViewData() {
        try {
            List<DistrictMrrViewReport> reports = districtMrrViewRepository.findAll();
            for (DistrictMrrViewReport report : reports) {
                DistrictMrrViewReportBackup backup = new DistrictMrrViewReportBackup();
                BeanUtils.copyProperties(report, backup);
                backup.setBackupDate(LocalDate.now());
                districtMrrViewReportBackupRepository.save(backup);
            }
            logger.info("Successfully backed up District Mrr Report View data.");
        } catch (Exception e) {
            logger.error("Error occurred while backing up District Mrr View data: ", e);
        }
    }

    public void backupDistrictRcmViewData() {
        try {
            List<DistrictRcmsReportDTO> reports = districtRcmsReportBackup.findAll();
            for (DistrictRcmsReportDTO report : reports) {
                DistrictRcmsReportBackup backup = new DistrictRcmsReportBackup();
                BeanUtils.copyProperties(report, backup);
                backup.setBackupDate(LocalDate.now());
                districtRcmsReportBackupRepository.save(backup);
            }
            logger.info("Successfully backed up District Rcms Report View data.");
        } catch (Exception e) {
            logger.error("Error occurred while backing up District Rcms View data: ", e);
        }
    }

    public void backupDistrictSurveyResurveyViewData() {
        try {
            List<DistrictSurveyResurveyViewReport> reports = districtSurveyResurveyRepository.findAll();
            for (DistrictSurveyResurveyViewReport report : reports) {
                DistrictSurveyResurveyViewReportBackup backup = new DistrictSurveyResurveyViewReportBackup();
                BeanUtils.copyProperties(report, backup);
                backup.setBackupDate(LocalDate.now());
                districtSurveyResurveyViewReportBackupRepository.save(backup);
            }
            logger.info("Successfully backed up District Survey Resurvey Report View data.");
        } catch (Exception e) {
            logger.error("Error occurred while backing up District Survey Resurvey View data: ", e);
        }
    }

    public void backupDistrictMapDigitizationViewData() {
        try {
            List<DistrictMapDigitizationReport> reports = districtMapViewRepository.findAll();
            for (DistrictMapDigitizationReport report : reports) {
                DistrictMapDigitizationReportBackup backup = new DistrictMapDigitizationReportBackup();
                BeanUtils.copyProperties(report, backup);
                backup.setBackupDate(LocalDate.now());
                districtMapDigitizationReportBackupRepository.save(backup);
            }
            logger.info("Successfully backed up District Map Digitization Report View data.");
        } catch (Exception e) {
            logger.error("Error occurred while backing up District Map Digitization View data: ", e);
        }
    }
    public void backupDistrictMISDataEntryData() {
        try {
            List<DistrictMISDataEntry> reports = districtMISDataEntryRepository.findAll();
            for (DistrictMISDataEntry report : reports) {
                if (report.getUpdateOnDate() != null && report.getUpdateOnDate().toLocalDate().equals(LocalDate.now())) {
                    DistrictMISDataEntryBackup backup = new DistrictMISDataEntryBackup();
                    BeanUtils.copyProperties(report, backup);
                    backup.setBackupDate(LocalDate.now());
                    misDataEntryBackupRepository.save(backup);
                }
            }
            logger.info("Successfully backed up District MIS Data Entry data.");
        } catch (Exception e) {
            logger.error("Error occurred while backing up District MIS Data Entry data: ", e);
        }
    }

    public void backupStateMISDataEntry() {
        try {
            List<StateMISDataEntry> reports = stateMISDataEntryRepository.findAll();
            for (StateMISDataEntry report : reports) {
                if (report.getUpdateOnDate() != null && report.getUpdateOnDate().equals(LocalDate.now())) {
                    StateMISDataEntryBackup backup = new StateMISDataEntryBackup();
                    BeanUtils.copyProperties(report, backup);
                    backup.setBackupDate(LocalDate.now());
                    stateMISDataEntryBackupRepository.save(backup);
                }
            }
            logger.info("Successfully backed up State MIS Data Entry data.");
        } catch (Exception e) {
            logger.error("Error occurred while backing up State MIS Data Entry data: ", e);
        }
    }

    public void backupStateRegistrationSystem() {
        try {
            List<StateRegistrationSystem> reports = igrMISDataEntryRepository.findAll();
            for (StateRegistrationSystem report : reports) {
                if (report.getUpdateOnDate() != null && report.getUpdateOnDate().equals(LocalDate.now())) {
                    StateRegistrationSystemBackup backup = new StateRegistrationSystemBackup();
                    BeanUtils.copyProperties(report, backup);
                    backup.setBackupDate(LocalDate.now());
                    stateRegistrationSystemBackupRepository.save(backup);
                }
            }
            logger.info("Successfully backed up State Registration System Data Entry data.");
        } catch (Exception e) {
            logger.error("Error occurred while backing up State Registration SystemData Entry data: ", e);
        }
    }


}