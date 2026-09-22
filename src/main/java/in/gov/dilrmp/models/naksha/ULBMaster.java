package in.gov.dilrmp.models.naksha;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ulbmaster")
public class ULBMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "muser_id")
    private Long muser_id;

    @Column(name = "ulb_name")
    private String ulb_name;
    @Column(name = "user_name")
    private String user_name;

    @Column(name = "state_id")
    private Long state_id;

    @Column(name = "state_name")
    private String state_name;

    @Column(name = "district_id")
    private Long district_id;

    @Column(name = "district_name")
    private String district_name;

    @Column(name = "zone_id")
    private Integer zoneID;
    @Column(name = "zone_name")
    private String zoneName;

    @Column(name = "gd_name")
    private String gdName;

    @Column(name = "gd_id")
    private Integer gdID;

    @Column(name = "flying_zone")
    private Integer flyingZone;



    // Newly added columns for Population, Survey Areas:
    @Column(name = "population")
    private Long population;  // Population for the ULB

    @Column(name = "field_survey_area")
    private Double fieldSurveyArea;  // Field Survey Area in sq.km.

    @Column(name = "aerial_survey_area")
    private Double aerialSurveyArea;  // Aerial Survey Area in sq.km.

    @Column(name = "buffer_grid_area_flying")
    private Double bufferGridAreaForFlying;  // Buffer/Grid Area for Flying in sq.km.

    // 🆕 Newly added columns for Teams sanctioned  and Rovers Sanctioned
    @Column(name = "no_of_teams")
    private Integer noOfTeams;

    @Column(name = "no_of_rovers")
    private Integer noOfRovers;

}
