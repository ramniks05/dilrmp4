package in.gov.dilrmp.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Component
public class DateTimeFormatterUtility {
    public String formatDate(LocalDate date) {
        return date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    public LocalDate parseDate(String dateString) {
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    public LocalDateTime getNow(){
        LocalDateTime now = LocalDateTime.now();
        return now;
    }



}
