package in.gov.dilrmp.models.dataEntryModel;

import in.gov.dilrmp.models.administrativeBoundry.State;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

import in.gov.dilrmp.models.administrativeBoundry.District;
import in.gov.dilrmp.models.user.User;

@Data
@Entity
@Table(name = "district_mis_data_entry",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"district_id"})})
public class DistrictMISDataEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;



    @Column(name = "total_tehsils", columnDefinition = "int default 0")
    private Integer totalTehsils = 0;

    @Column(name = "total_villages", columnDefinition = "int default 0")
    private Integer totalVillages = 0;

    // Component 1: Computerization of Land Records (CLR)
    @Column(name = "villages_computerization_completed", columnDefinition = "int default 0")
    private Integer villagesComputerizationCompleted = 0;

    @Column(name = "total_ror", columnDefinition = "int default 0")
    private Integer totalRoR = 0;

    @Column(name = "ror_computerized", columnDefinition = "int default 0")
    private Integer rorComputerized = 0;

    @Column(name = "total_land_owners", columnDefinition = "int default 0")
    private Integer totalLandOwners = 0;

    @Column(name = "gender_based_land_ownership",columnDefinition = "boolean default false")
    private Boolean genderBasedLandOwnership=false;

    @Column(name = "total_male_land_owners",columnDefinition = "int default 0")
    private Integer totalMaleLandOwners = 0;

    @Column(name = "total_female_land_owners",columnDefinition = "int default 0")
    private Integer totalFemaleLandOwners = 0;

    @Column(name = "csc_kiosk_online",columnDefinition = "int default 0")
    private Integer cscKioskOnline = 0;

    @Column(name = "digitally_signed",columnDefinition = "int default 0")
    private Integer digitallySigned = 0;

    @Column(name = "online_mutation",columnDefinition = "int default 0")
    private  Integer onlineMutation = 0;

    @Column(name = "auto_triggred_mutation",columnDefinition = "int default 0")
    private Integer autoTriggredMutation = 0;

    @Column(name = "ror_bank",columnDefinition = "int default 0")
    private Integer rorBank = 0;


    @Column(name = "tehsils_civil_court",columnDefinition = "int default 0")
    private Integer tehsilsCivilCourt = 0;

    // Component 2: Map Digitization
    @Column(name = "total_cadastral_maps",columnDefinition = "int default 0")
    private Integer totalCadastralMaps = 0;

    @Column(name = "digitized_cadastral_maps",columnDefinition = "int default 0")
    private Integer digitizedCadastralMaps = 0;

    @Column(name = "total_fmbs",columnDefinition = "int default 0")
    private Integer totalFMBs = 0;

    @Column(name = "digitized_fmbs",columnDefinition = "int default 0")
    private Integer digitizedFMBs = 0;

    @Column(name = "total_tippans",columnDefinition = "int default 0")
    private Integer totalTippans = 0;

    @Column(name = "digitized_tippans",columnDefinition = "int default 0")
    private Integer digitizedTippans = 0;

    @Column(name = "villages_linked_with_ror",columnDefinition = "int default 0")
    private Integer villagesLinkedWithRoR = 0;

    @Column(name = "maps_updated_based_on_mutation",columnDefinition = "boolean default false")
    private Boolean mapsUpdatedBasedOnMutation=false;

    @Column(name = "maps_update_period")
    private Date mapsUpdatePeriod;

    @Column(name = "villages_georeferenced",columnDefinition = "int default 0")
    private Integer villagesGeoreferenced = 0;

    @Column(name = "georeferenced_maps",columnDefinition = "int default 0")
    private Integer georeferencedMaps = 0;

    @Column(name = "total_land_parcels",columnDefinition = "int default 0")
    private Integer totalLandParcels = 0;

    @Column(name = "georeferenced_land_parcels",columnDefinition = "int default 0")
    private Integer georeferencedLandParcels = 0;

    @Column(name = "villages_with_ulipn",columnDefinition = "int default 0")
    private Integer villagesWithULIPN = 0;

    @Column(name = "land_parcels_with_ulipn",columnDefinition = "int default 0")
    private Integer landParcelsWithULIPN = 0;

    //Component 3: Computerization of Registration (SRO)

    @Column(name = "total_sro",columnDefinition = "int default 0")
    private Integer totalSRO = 0;

    @Column(name = "sro_computerized",columnDefinition = "int default 0")
    private Integer sroComputerized = 0;

    @Column(name = "sro_lr",columnDefinition = "int default 0")
    private Integer sroLR = 0;

    @Column(name = "sro_tehsil_linkage",columnDefinition = "int default 0")
    private Integer sroTehsilLinkage = 0;

    @Column(name = "legacy_records",columnDefinition = "int default 0")
    private Integer legacyRecords = 0;

    @Column(name = "legacy_records_search",columnDefinition = "int default 0")
    private Integer legacyRecordsSearch = 0;

    // Component 4: Modern Record Room (MRR)
    @Column(name = "mrr_sanctioned",columnDefinition = "int default 0")
    private Integer mrrSanctioned = 0;

    @Column(name = "mrr_completed",columnDefinition = "int default 0")
    private Integer mrrCompleted = 0;

    // Component 5: Survey/Re-Survey under NLRMP/DILRMP
    @Column(name = "total_rural_revenue_area", columnDefinition = "double precision default 0.0")
    private Double totalRuralRevenueArea =0.0;

    @Column(name = "area_sanctioned_for_survey", columnDefinition = "double precision default 0.0")
    private Double areaSanctionedForSurvey = 0.0;

    @Column(name = "villages_drone_flying_completed",columnDefinition = "int default 0")
    private Integer villagesDroneFlyingCompleted = 0;

    @Column(name = "area_drone_flying_completed", columnDefinition = "double precision default 0.0")
    private Double areaDroneFlyingCompleted = 0.0;

    @Column(name = "villages_map_1_generated",columnDefinition = "int default 0")
    private Integer villagesMap1Generated = 0;

    @Column(name = "area_surveyed", columnDefinition = "double precision default 0.0")
    private Double areaSurveyed = 0.0;

    @Column(name = "villages_draft_map_published",columnDefinition = "int default 0")
    private Integer villagesDraftMapPublished = 0;

    @Column(name = "villages_final_promulgation_done",columnDefinition = "int default 0")
    private Integer villagesFinalPromulgationDone = 0;

    @Column(name = "villages_survey_not_started",columnDefinition = "int default 0")
    private Integer villagesSurveySanctionNotStarted = 0;

    @Column(name = "area_survey_not_started", columnDefinition = "double precision default 0.0")
    private Double areaSurveySanctionNotStarted = 0.0;

    // Component 6: Computerization of Revenue Court Management System (e-RCMS)
    @Column(name = "total_revenue_courts",columnDefinition = "int default 0")
    private Integer totalRevenueCourts = 0;

    @Column(name = "revenue_courts_computerized",columnDefinition = "int default 0")
    private Integer revenueCourtsComputerized = 0;

    @Column(name = "land_record_integration",columnDefinition = "int default 0")
    private Integer landRecordIntegration = 0;

    @Column(name = "sro_integration",columnDefinition = "int default 0")
    private Integer sroIntegration = 0;

    // Component 7: Consent-based linkage of Aadhaar with Record of Rights (RoR)
    @Column(name = "villages_with_ror_linked_aadhaar",columnDefinition = "int default 0")
    private Integer villagesWithRoRLinkedAadhaar = 0;

    @Column(name = "villages_with_100_percent_ror_linked_aadhaar",columnDefinition = "int default 0")
    private Integer villagesWith100PercentRoRLinkedAadhaar = 0;

    @Column(name = "land_owners_aadhar",columnDefinition = "int default 0")
    private Integer landOwnersAadhar = 0;

    @Column(name = "ror_linked_with_aadhaar",columnDefinition = "int default 0")
    private Integer rorLinkedWithAadhaar = 0;

    @Column(name = "ror_linked_with_mobile_number",columnDefinition = "int default 0")
    private Integer rorLinkedWithMobileNumber = 0;

      // Additional fieldsa
    @Column(name = "last_update_on_date")
    private LocalDateTime updateOnDate;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    
    //Insert or Update Medium DE Forms:-1 and API -2
    @Column(name = "data_source_type")
    private Integer dataSourceType;
    
    //Count How many times update increment value by 1 on each update initial value 1
    @Column(name = "version")
    private Integer version;

    @Column(name = "created_on_date")
    private LocalDateTime createdOnDate;

    @Transient
    private String  mapsUpdatePeriodDate;

}
