package in.gov.dilrmp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
public class DateUtils {
    // Define the date format as a constant
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String STRING_DATE_FORMAT = "dd-MM-yyyy";

    public static final String STRING_DATE_FORMATS = "dd-MMM-yyyy";


    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // Utility method to format a Date to String
    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateFormat.format(date);
    }

    // Utility method to convert a String to a Date
    public static Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Utility method to convert a String to a LocalDateTime
    public static LocalDateTime parseStringToLocalDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        try {
            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace(); // Handle the exception based on your needs
            return null; // Or throw a custom exception
        }
    }

    // Utility method to convert a LocalDateTime to a String
    public static String formatLocalDateTimeToString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return dateTime.format(formatter);
    }

    public static String formatDisplayDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return dateTime.format(formatter);
    }

    // Utility method to get the current LocalDateTime
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }


    public static String formatLocalDateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(STRING_DATE_FORMAT);
        return date.format(formatter);

    }

    public static String formatDateInString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(STRING_DATE_FORMATS);
        return dateFormat.format(date);
    }
}
