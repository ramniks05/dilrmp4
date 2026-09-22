package in.gov.dilrmp.utils;

import java.util.List;

public class IECActivityDropdowns {

    // --------------------------------------
    // [1] IEC Material Status
    // --------------------------------------
    public enum IECMaterialStatus {
        UPLOADED(1, "Uploaded"),
        NOT_UPLOADED(2, "Not Uploaded");

        private final int code;
        private final String description;

        IECMaterialStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }

        public String getDescription() { return description; }

        public static IECMaterialStatus fromCode(int code) {
            for (IECMaterialStatus status : values()) {
                if (status.code == code) return status;
            }
            throw new IllegalArgumentException("Invalid IECMaterialStatus code: " + code);
        }

        public static List<IECMaterialStatus> getAll() {
            return List.of(values());
        }
    }

    // --------------------------------------
    // [2] Type of IEC Media Uploaded
    // --------------------------------------
    public enum IECMediaType {
        PRINT(1, "Print"),
        DIGITAL(2, "Digital"),
        AUDIO(3, "Audio");

        private final int code;
        private final String description;

        IECMediaType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }

        public String getDescription() { return description; }

        public static IECMediaType fromCode(int code) {
            for (IECMediaType type : values()) {
                if (type.code == code) return type;
            }
            throw new IllegalArgumentException("Invalid IECMediaType code: " + code);
        }

        public static List<IECMediaType> getAll() {
            return List.of(values());
        }
    }

    // --------------------------------------
    // [3] Variety of IEC Media Uploaded (multi-select, comma-separated)
    // --------------------------------------
    public enum IECMediaVariety {
        POSTERS(1, "Posters"),
        BANNERS(2, "Banners"),
        BROCHURES(3, "Brochures"),
        VIDEOS(4, "Videos"),
        SOCIAL_MEDIA(5, "Social Media"),
        CREATIVES(6, "Creatives"),
        PRESENTATION(7, "Presentation"),
        RADIO_SPOTS(8, "Radio Spots");


        private final int code;
        private final String description;

        IECMediaVariety(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }

        public String getDescription() { return description; }

        public static IECMediaVariety fromCode(int code) {
            for (IECMediaVariety variety : values()) {
                if (variety.code == code) return variety;
            }
            throw new IllegalArgumentException("Invalid IECMediaVariety code: " + code);
        }

        public static List<IECMediaVariety> getAll() {
            return List.of(values());
        }
    }

    // --------------------------------------
    // [4] IEC Activity Status
    // --------------------------------------
    public enum IECActivityStatus {
        PLANNED(1, "Planned"),
        IN_PROGRESS(2, "In-Progress"),
        COMPLETED(3, "Completed");


        private final int code;
        private final String description;

        IECActivityStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }

        public String getDescription() { return description; }

        public static IECActivityStatus fromCode(int code) {
            for (IECActivityStatus status : values()) {
                if (status.code == code) return status;
            }
            throw new IllegalArgumentException("Invalid IECActivityStatus code: " + code);
        }

        public static List<IECActivityStatus> getAll() {
            return List.of(values());
        }
    }

    // --------------------------------------
    // [6] IEC Activity Conducted
    // --------------------------------------
    public enum IECActivityConducted {
        YES(1, "Yes"),
        NO(2, "No"),
        UNDER_PROCESS(3, "Under Process");

        private final int code;
        private final String description;

        IECActivityConducted(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }

        public String getDescription() { return description; }

        public static IECActivityConducted fromCode(int code) {
            for (IECActivityConducted status : values()) {
                if (status.code == code) return status;
            }
            throw new IllegalArgumentException("Invalid IECActivityConducted code: " + code);
        }

        public static List<IECActivityConducted> getAll() {
            return List.of(values());
        }
    }

    // --------------------------------------
    // [7] Type of Activity Conducted (multi-select, comma-separated)
    // --------------------------------------
    public enum IECActivityType {
        WORKSHOP(1, "Workshop"),
        PUBLIC_MEETING(2, "Public Meeting"),
        GRAM_SABHA(3, "Gram Sabha"),
        PRESS_CONFERENCE(4, "Press Conference"),
        MEDIA_BRIEFING(5, "Media Briefing"),
        HOARDING(6, "Hoarding"),
        BILLBOARD_DISPLAY(7, "Billboard Display"),
        PAMPHLET(8, "Pamphlet"),
        LEAF_DISTRIBUTION(9, "Leaf Distribution"),
        RADIO_JINGLE(10, "Radio Jingle"),
        TV_COMMERCIAL_BROADCAST(11, "TV Commercial Broadcast"),
        SOCIAL_MEDIA_CAMPAIGN(12, "Social Media Campaign"),
        PARTICIPATION_IN_FAIR(13, "Participation in Fair"),
        EXHIBITION(14, "Exhibition"),
        MOBILE_VAN_CAMPAIGN(15, "Mobile Van Campaign"),
        RAILWAY_STATION_CAMPAIGN(16, "Railway Station Campaign"),
        MOVIE_THEATRE(17, "Movie Theatre"),
        NUKKAD_NATAK(18, "Nukkad Natak"),
        OTHERS(19, "Others");


        private final int code;
        private final String description;

        IECActivityType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }

        public String getDescription() { return description; }

        public static IECActivityType fromCode(int code) {
            for (IECActivityType type : values()) {
                if (type.code == code) return type;
            }
            throw new IllegalArgumentException("Invalid IECActivityType code: " + code);
        }

        public static List<IECActivityType> getAll() {
            return List.of(values());
        }
    }


}
