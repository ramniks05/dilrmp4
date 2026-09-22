package in.gov.dilrmp.models.dataEntryModel;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * //v5 Generic DoLR MIS — fields filled only by DoLR (not State/IGR/District forms).
 * One row per State/UT.
 */
@Entity
@Table(name = "dolr_mis_data_entry",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"state_id"})})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DolrMisDataEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    /** Legacy Digitization — sanctioned pages under DILRMP (report column C). */
    @Column(name = "legacy_dilrmp_sanctioned_pages", columnDefinition = "int default 0")
    private Integer legacyDilrmpSanctionedPages = 0;

    /** SRO Modernization — SROs sanctioned under DILRMP (report column C). */
    @Column(name = "sros_dilrmp_sanctioned", columnDefinition = "int default 0")
    private Integer srosDilrmpSanctioned = 0;

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
