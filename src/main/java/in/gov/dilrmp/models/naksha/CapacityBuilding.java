package in.gov.dilrmp.models.naksha;

import in.gov.dilrmp.models.administrativeBoundry.State;
import in.gov.dilrmp.models.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Entity
@Table(name = "capacity_building")
@Data
public class CapacityBuilding {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "state_id", nullable = false)
        private State state;

        @ManyToOne
        @JoinColumn(name = "muser_id")
        private User muser;

        @Column(name = "name_of_coe")
        private String nameOfCoE;

        @Column(name = "nigst_master_trainers")
        private Integer nigstMasterTrainers;

        @Column(name = "coe_master_trainers")
        private Integer coeMasterTrainers;

        @Column(name = "field_teams_sanctioned")
        private Integer fieldTeamsSanctioned;

        @Column(name = "members_trained")
        private Integer membersTrained;

        @Column(name = "create_on_date")
        private LocalDate createOnDate;

        @Column(name = "last_update_on_date")
        private LocalDate updateOnDate;

        @Column(name = "total_master_trainers")
        private Integer totalMasterTrainers;

        @Column(name = "members_to_be_trained")
        private Integer membersToBeTrained;

        @Column(name = "officers_trained")
        private Integer officersTrained;

        @Transient
        private Double fieldTeamsTrained;

        @Transient
        private Double percentageTrained;

        @Transient
        private String formatedfieldTeamsTrained;

        @Transient
        private String formatedpercentageTrained;




        @Transient
        private Integer GrandTotalNigstMasterTrainers;

        @Transient
        private Integer GrandTotalCoeMasterTrainers;

        @Transient
        private Integer GrandTotalTotalMasterTrainers;

        @Transient
        private Integer GrandTotalFieldTeamsSanctioned;

        @Transient
        private Integer GrandTotalMembersTrained;

        @Transient
        private Integer GrandTotalMembersToBeTrained;


        public Double getFieldTeamsTrained() {
                return (membersToBeTrained != null ? membersToBeTrained / 3.0 : 0.0);
        }

        public Double getPercentageTrained() {
                Double trained = getFieldTeamsTrained();
                if (fieldTeamsSanctioned != null && fieldTeamsSanctioned > 0) {
                        double percent = (trained / fieldTeamsSanctioned) * 100;
                        return Math.round(percent * 100.0) / 100.0;
                }
                return 0.0;
        }




}

