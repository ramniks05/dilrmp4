package in.gov.dilrmp.Scheduler.models;

import in.gov.dilrmp.models.administrativeBoundry.District;
import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
public class DistrictMISDataEntryBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;
    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;
    private Integer totalTehsils;
    private Integer totalVillages;

    // Component 1: Computerization of Land Records (CLR)
    private Integer villagesComputerizationCompleted;
    private Integer totalRoR;
    private Integer rorComputerized;
    //v5 CLR: S.No. [6.1] Number of RoR with Cadastral Map
    private Integer rorWithCadastralMap;
    private Integer totalLandOwners;
    private Boolean genderBasedLandOwnership;
    private Integer totalMaleLandOwners;
    private Integer totalFemaleLandOwners;
    private Integer cscKioskOnline;
    private Integer digitallySigned;
    private  Integer onlineMutation;
    private Integer autoTriggredMutation;
    private Integer rorBank;
    private Integer tehsilsCivilCourt;

    // Component 2: Map Digitization
    private Integer totalCadastralMaps;
    private Integer digitizedCadastralMaps;
    private Integer totalFMBs;
    private Integer digitizedFMBs;
    private Integer totalTippans;
    private Integer digitizedTippans;
    private Integer villagesLinkedWithRoR;
    private Boolean mapsUpdatedBasedOnMutation;
    private Date mapsUpdatePeriod;
    private Integer villagesGeoreferenced;
    private Integer georeferencedMaps;
    private Integer totalLandParcels;
    private Integer georeferencedLandParcels;
    private Integer villagesWithULIPN;
    private Integer landParcelsWithULIPN;

    //v5 Map Digitization: S.No. [11.1] Total Damaged/Missing Maps (Cadastral; validated vs [11] only)
    private Integer totalDamagedMissingMaps;

    //Component 3: Modernization of Registration Office (SRO)
    private Integer totalSRO;
    private Integer sroComputerized;
    private Integer srosModernisedStateFunds;
    private Integer srosModernisedDilrmpFunds;
    private Integer sroLR;
    private Integer sroTehsilLinkage;
    private Integer legacyRecords;
    private Integer legacyRecordsSearch;

    // Component 4: Modern Record Room (MRR)
    private Integer mrrSanctioned;
    private Integer mrrCompleted;
    // Component 5: Survey/Re-Survey under NLRMP/DILRMP
    private Double totalRuralRevenueArea;
    private Double areaSanctionedForSurvey;
    private Integer villagesDroneFlyingCompleted;
    private Double areaDroneFlyingCompleted;
    private Integer villagesMap1Generated;
    private Double areaSurveyed;
    private Integer villagesDraftMapPublished;
    private Integer villagesFinalPromulgationDone;
    private Integer villagesSurveySanctionNotStarted;
    private Double areaSurveySanctionNotStarted;
    // Component 6: Computerization of Revenue Court Management System (e-RCMS)
    private Integer totalRevenueCourts;
    private Integer revenueCourtsComputerized;
    private Integer landRecordIntegration;
    private Integer sroIntegration;
    // Component 7: Consent-based linkage of Aadhaar with Record of Rights (RoR)
    private Integer villagesWithRoRLinkedAadhaar;
    private Integer villagesWith100PercentRoRLinkedAadhaar;
    private Integer landOwnersAadhar;
    private Integer rorLinkedWithAadhaar;
    private Integer rorLinkedWithMobileNumber;
    //v5 Aadhaar: S.No. [43] Number of RoR linked with Address
    private Integer rorLinkedWithAddress;
    //v5 Aadhaar: Land owners [45]-[46]
    private Integer landOwnersLinkedWithMobile;
    private Integer landOwnersLinkedWithAddress;
    //v5 Legacy Digitization
    private Integer legacyTotalPages;
    private Integer legacyDigitisedStateFundsPages;
    private Integer legacyDilrmpSanctionedPages;
    private Integer legacyDigitisedDilrmpFundsPages;
    private Integer legacyTotalDigitisedPages;
    private Integer legacyDigitisedUptoYear;
    //v5 Legacy Revenue Records Digitisation
    private Integer revenueLegacyTotalPages;
    private Integer revenueLegacyDigitisedStateFundsPages;
    private Integer revenueLegacyDigitisedDilrmpFundsPages;
    private Integer revenueLegacyDigitisedUptoYear;
    // Additional fieldsa
    private LocalDateTime updateOnDate;
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    //Insert or Update Medium DE Forms:-1 and API -2
    private Integer dataSourceType;
    //Count How many times update increment value by 1 on each update initial value 1
    private Integer version;
    private LocalDateTime createdOnDate;
    private LocalDate backupDate;
}
