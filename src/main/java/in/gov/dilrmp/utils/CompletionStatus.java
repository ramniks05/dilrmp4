package in.gov.dilrmp.utils;



import java.util.List;



public enum CompletionStatus {


    NOT_STARTED(1, "Not Started"),
    UNDER_PROCESS(2, "Under Progress"),  // Updated label
    COMPLETED(3, "Completed"),
    UNDER_CORRECTION(4, "Under Correction"),
    COMPLETED_POST_CORRECTION(5, "Completed Post Correction"),

    // For QA/QC
    ACCEPTED(6, "Accepted"),
    RETURNED(7, "Returned"),
    ACCEPTED_AFTER_CORRECTION(8, "Accepted after Correction"),
    RE_FLY_UNDER_PROGRESS(9, "Re-Fly Under Progress" ),
    RE_FLY_COMPLETED(10, "Re-Fly Completed"),
    NOT_AVAILABLE(11, "N/A");




    private final int code;
    private final String description;

    CompletionStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static CompletionStatus fromCode(int code) {
        for (CompletionStatus status : CompletionStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid CompletionStatus code: " + code);
    }

    // ⬇️ List for Vendor UI Dropdown
    public static List<CompletionStatus> getVendorStatuses() {
        return List.of(
                NOT_STARTED,
                UNDER_PROCESS,
                COMPLETED,
                UNDER_CORRECTION,
                COMPLETED_POST_CORRECTION
        );
    }

    // ⬇️ List for QA/QC UI Dropdown
    public static List<CompletionStatus> getQaQcStatuses() {
        return List.of(
                NOT_STARTED,
                UNDER_PROCESS,
                ACCEPTED,
                RETURNED,
                ACCEPTED_AFTER_CORRECTION
        );
    }


    // ⬇️ List for Vendor UI Dropdown
    public static List<CompletionStatus> getTechStatuses() {
        return List.of(
                NOT_STARTED,
                UNDER_PROCESS,
                RE_FLY_UNDER_PROGRESS,
                COMPLETED,
                RE_FLY_COMPLETED

        );
    }


}
