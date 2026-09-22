package in.gov.dilrmp.utils;

public enum Agency {
    AARVEE_ASSOCIATES("Aarvee Associates Architects Engineers & Consultant Pvt. Ltd."),
    ARAHAS_TECHNOLOGIES("Arahas Technologies Pvt. Ltd."),
    AZURE_CLOUD("Azure Cloud"),
    CE_INFO_SYSTEMS("C.E. Info Systems Limited"),
    DRONE_DESTINATION("Drone Destination Ltd."),
    GADUDA_UAV_SOFT_SOLUTIONS("Garuda UAV Soft Solutions Pvt. Ltd."),
    LTI_MINDTREE("L.T.I Mindtree Ltd."),
    PIONEER_FOUNDATION_ENGINEERS("Pioneer Foundation Engineers Pvt. Ltd."),
    SOI("SoI"),
    STESALIT_SYSTEMS("Stesalit Systems Ltd."),

    ALMONDZ_GLOBAL("Almondz Global Infra-Consultant Ltd.");


    private final String displayName;

    Agency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Add this method
    public static boolean containsDisplayName(String name) {
        for (Agency agency : values()) {
            if (agency.displayName.equalsIgnoreCase(name.trim())) {
                return true;
            }
        }
        return false;
    }

}
