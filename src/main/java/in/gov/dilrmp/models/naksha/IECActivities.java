package in.gov.dilrmp.models.naksha;
import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.user.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
@Entity
@Table(name = "iec_activity")
@Data
public class IECActivities {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "state_id", nullable = false)
        private State state;

        @Column(name = "state_name", length = 255)
        private String stateName;

        @ManyToOne
        @JoinColumn(name = "muser_id")
        private User muser;

        // Store enum code instead of string description
        @Column(name = "iec_material_status")
        private Integer iecMaterialStatus; // e.g., 1 or 2 (code from IECMaterialStatus enum)


        @Column(name = "iec_material_link")
        private String iecMaterialLink;
        @Column(name = "iec_media_type")
        private Integer iecMediaType; // e.g., 1, 2, 3 from IECMediaType enum

        @Column(name = "iec_media_variety")
        private Integer iecMediaVariety; // This may remain String if multiple values as text (or consider storing as CSV of codes)

        @Column(name = "iec_activity_status")
        private Integer iecActivityStatus; // Integer code from IECActivityStatus enum

        @Column(name = "iec_activity_link", length = 512)
        private String iecActivityLink;

        @Column(name = "iec_activity_conducted")
        private Integer iecActivityConducted; // Integer code from IECActivityConducted enum

        @Column(name = "iec_activity_type")
        private Integer iecActivityType;
        // If multiple types, consider storing comma separated codes as String or use a separate join table for normalization


        @Column(name = "other_activity", length = 255)
        private String otherActivity;

        @Column(name = "create_on_date")
        private LocalDate createOnDate;

        @Column(name = "last_update_on_date")
        private LocalDate updateOnDate;
}
