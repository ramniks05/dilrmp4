package in.gov.dilrmp.utils;
import java.util.List;
public class DropdownOptions {

    // --------------------------------------
    // Rover Procurement Status
    // --------------------------------------
    public enum RoverProcurementStatus {
        PROCURED(1, "Procured"),
        ALREADY_AVAILABLE(2, "Already Available (for NAKSHA)"),
        RENTAL(3, "Rental"),
        INITIATED(4, "Initiated"),
        UNDER_PROGRESS(5, "Under Progress"),
        NOT_STARTED(6, "Not Started"),
        NOT_AVAILABLE(7, "N/A");

        private final int code;
        private final String description;




        RoverProcurementStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }

        public String getDescription() { return description; }

        public static RoverProcurementStatus fromCode(int code) {
            for (RoverProcurementStatus status : values()) {
                if (status.code == code) return status;
            }
            throw new IllegalArgumentException("Invalid RoverProcurementStatus code: " + code);
        }

        public static List<RoverProcurementStatus> getAll() {
            return List.of(values());
        }
    }

    // --------------------------------------
    // Legal Framework for Urban Survey
    // --------------------------------------
    public enum LegalFrameworkUrbanSurveyStatus {
        ADEQUATE_PROVISIONS_AVAILABLE(1, "Adequate Provisions Available"),
        AMENDMENTS_REQUIRED(2, "Amendments Required");

        private final int code;
        private final String description;

        LegalFrameworkUrbanSurveyStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }

        public String getDescription() { return description; }

        public static LegalFrameworkUrbanSurveyStatus fromCode(int code) {
            for (LegalFrameworkUrbanSurveyStatus status : values()) {
                if (status.code == code) return status;
            }
            throw new IllegalArgumentException("Invalid LegalFrameworkUrbanSurveyStatus code: " + code);
        }

        public static List<LegalFrameworkUrbanSurveyStatus> getAll() {
            return List.of(values());
        }
    }

    // --------------------------------------
    // Status of Legal Framework Amendment
    // --------------------------------------
    public enum LegalFrameworkAmendmentStatus {
        INITIATED(1, "Initiated"),
        NOT_INITIATED(2, "Not Initiated"),
        UNDER_PROGRESS(3, "Under Progress");

        private final int code;
        private final String description;

        LegalFrameworkAmendmentStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }

        public String getDescription() { return description; }

        public static LegalFrameworkAmendmentStatus fromCode(int code) {
            for (LegalFrameworkAmendmentStatus status : values()) {
                if (status.code == code) return status;
            }
            throw new IllegalArgumentException("Invalid LegalFrameworkAmendmentStatus code: " + code);
        }

        public static List<LegalFrameworkAmendmentStatus> getAll() {
            return List.of(values());
        }
    }

    // --------------------------------------
    // SLC Meeting Conducted
    // --------------------------------------
    public enum SLCMeetingStatus {
        YES(1, "Yes"),
        NO(2, "No");

        private final int code;
        private final String description;

        SLCMeetingStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }

        public String getDescription() { return description; }

        public static SLCMeetingStatus fromCode(int code) {
            for (SLCMeetingStatus status : values()) {
                if (status.code == code) return status;
            }
            throw new IllegalArgumentException("Invalid SLCMeetingStatus code: " + code);
        }

        public static List<SLCMeetingStatus> getAll() {
            return List.of(values());
        }
    }


    public enum SurveyUnitType {
        WARD(1, "Ward"),
        BLOCK(2, "Block"),
        COLONY(3, "Colony"),
        SECTOR(4, "Sector"),
        REVENUE_VILLAGE(5, "Revenue Village"),
        TILE(6, "Tile"),
        OTHERS(7, "Others");

        private final int code;
        private final String description;

        SurveyUnitType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }

        public String getDescription() { return description; }

        public static SurveyUnitType fromCode(int code) {
            for (SurveyUnitType type : values()) {
                if (type.code == code) return type;
            }
            throw new IllegalArgumentException("Invalid SurveyUnitType code: " + code);
        }

        public static List<SurveyUnitType> getAll() {
            return List.of(values());
        }
    }

    // --------------------------------------
    // Type of Survey Done
    // --------------------------------------
    public enum SurveyType {
        PROPERTY(1, "Property"),
        AREA(2, "Area");

        private final int code;
        private final String description;

        SurveyType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }

        public String getDescription() { return description; }

        public static SurveyType fromCode(int code) {
            for (SurveyType type : values()) {
                if (type.code == code) return type;
            }
            throw new IllegalArgumentException("Invalid SurveyType code: " + code);
        }

        public static List<SurveyType> getAll() {
            return List.of(values());
        }
    }

    // --------------------------------------
    // Type of Survey Done for Survey Unit
    // --------------------------------------
    public enum SurveyUnitSurveyType {
        PROPERTY(1, "Property"),
        AREA(2, "Area");

        private final int code;
        private final String description;

        SurveyUnitSurveyType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }

        public String getDescription() { return description; }

        public static SurveyUnitSurveyType fromCode(int code) {
            for (SurveyUnitSurveyType type : values()) {
                if (type.code == code) return type;
            }
            throw new IllegalArgumentException("Invalid SurveyUnitSurveyType code: " + code);
        }

        public static List<SurveyUnitSurveyType> getAll() {
            return List.of(values());
        }
    }

    // --------------------------------------
    // Work Validation by Supervisory Officer
    // --------------------------------------
    public enum WorkValidationStatus {
        SURVEY_FOUND_CORRECT(1, "Survey found Correct"),
        SURVEY_FOUND_WITH_ERRORS(2, "Survey found with Errors");

        private final int code;
        private final String description;

        WorkValidationStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }

        public String getDescription() { return description; }

        public static WorkValidationStatus fromCode(int code) {
            for (WorkValidationStatus status : values()) {
                if (status.code == code) return status;
            }
            throw new IllegalArgumentException("Invalid WorkValidationStatus code: " + code);
        }

        public static List<WorkValidationStatus> getAll() {
            return List.of(values());
        }
    }



    // --------------------------------------
// Status of Ward Level Shapefile Sharing
// --------------------------------------
    public enum WardLevelShapefileStatus {
        SHARED(1, "Shared"),
        NOT_SHARED(2, "Not Shared");

        private final int code;
        private final String description;

        WardLevelShapefileStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }

        public String getDescription() { return description; }

        public static WardLevelShapefileStatus fromCode(int code) {
            for (WardLevelShapefileStatus status : values()) {
                if (status.code == code) return status;
            }
            throw new IllegalArgumentException("Invalid WardLevelShapefileStatus code: " + code);
        }

        public static List<WardLevelShapefileStatus> getAll() {
            return List.of(values());
        }
    }



}
