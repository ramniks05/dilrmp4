package in.gov.dilrmp.models.dataEntryModel;

import in.gov.dilrmp.models.administrativeBoundry.District;
import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DoLR sanctions entered per district.
 * Legacy Registered Documents and Legacy Revenue Records page sanctions.
 * One row per district.
 */
@Entity
@Table(name = "dolr_district_mis_data_entry",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"district_id"})})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DolrDistrictMisDataEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(name = "legacy_dilrmp_sanctioned_pages", columnDefinition = "int default 0")
    private Integer legacyDilrmpSanctionedPages = 0;

    @Column(name = "revenue_legacy_dilrmp_sanctioned_pages", columnDefinition = "int default 0")
    private Integer revenueLegacyDilrmpSanctionedPages = 0;

    @Column(name = "last_update_on_date")
    private LocalDate updateOnDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "version", columnDefinition = "int default 0")
    private Integer version = 0;

    @Column(name = "created_on_date")
    private LocalDate createdOnDate;
}
