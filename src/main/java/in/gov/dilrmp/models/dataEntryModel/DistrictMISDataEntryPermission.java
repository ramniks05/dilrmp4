package in.gov.dilrmp.models.dataEntryModel;

import in.gov.dilrmp.models.administrativeBoundry.District;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "district_mis_data_entry_permission",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"district_id"})})
public class DistrictMISDataEntryPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(name = "allow_decrease", nullable = false, columnDefinition = "boolean default false")
    private boolean allowDecrease = false;

    @Column(name = "decrease_allowed_until")
    private LocalDateTime decreaseAllowedUntil;
}
