package in.gov.dilrmp.services.naksha;


import in.gov.dilrmp.models.naksha.NakshaMISDataEntrySolrAndVender;
import in.gov.dilrmp.models.naksha.TimelineBackup;
import in.gov.dilrmp.repositories.naksha.NakshaMISDataVenderRepository;
import in.gov.dilrmp.repositories.naksha.TimelineBackupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class TimelineBackupService {

    @Autowired
    TimelineBackupRepository timelineBackupRepository;
    @Autowired
    NakshaMISDataVenderRepository nakshaMISDataVenderRepository;


    private static final Logger logger = LoggerFactory.getLogger(TimelineBackupService.class);

    public String timelineBackup(LocalDate today) {
        try {


            // ✅ Check if today's backup already exists
            boolean exists = timelineBackupRepository.existsByUpdateOnDate(today);

            if (exists) {
                String msg="already exists";
                logger.info(msg);
                return msg;
            }

            // ✅ Proceed with backup if not exists
            List<NakshaMISDataEntrySolrAndVender> nakshaMISDataEntrySolrAndVenders =
                    nakshaMISDataVenderRepository.findAll();

            for (NakshaMISDataEntrySolrAndVender solrAndVender : nakshaMISDataEntrySolrAndVenders) {
                TimelineBackup backup = new TimelineBackup();
                BeanUtils.copyProperties(solrAndVender, backup);
                backup.setBackupDate(today);
                backup.setTimelineMeetingDate(today);

                timelineBackupRepository.save(backup);
            }

            String msg = "Successfully backed up State Timeline View data for date: " + today;
            logger.info(msg);
            return msg;

        } catch (Exception e) {
            String errorMsg = "Error occurred while backing up Timeline data: " + e.getMessage();
            logger.error(errorMsg, e);
            return errorMsg;
        }
    }

    public List<String> getAllBackupDates() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);
        return timelineBackupRepository.findDistinctBackupDates()
                .stream()
                .map(date -> date.format(formatter)) // format each LocalDate
                .collect(Collectors.toList());
    }

}
