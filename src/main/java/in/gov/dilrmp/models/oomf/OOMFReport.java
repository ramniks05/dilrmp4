package in.gov.dilrmp.models.oomf;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "oomf")
public class OOMFReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "financial_year")
    private String financialYear;

    @Column(name = "quarter")
    private String quarter;

    @Column(name = "target1")
    private Integer target1; // Corresponds to "No. of RoRs computerized"

    @Column(name = "target2")
    private Integer target2; // Corresponds to "No. of Maps / FMBs / Tippans digitized"

    @Column(name = "target3")
    private Integer target3; // Corresponds to "RoRs integrated with cadastral Maps / FMBs / Tippans"

    @Column(name = "target4")
    private Integer target4; // Corresponds to "No. of villages having geo-referenced cadastral maps/FMBs"

    @Column(name = "target5")
    private Integer target5; // Corresponds to "No. of Districts with computerized registration"

    @Column(name = "target6")
    private Integer target6; // Corresponds to "No. of Districts with integration of RoRs and registration"

    @Column(name = "target7")
    private Integer target7; // Corresponds to "No. of Integrated Districts enabled for auto credit to farmer"

    @Column(name = "target8")
    private Integer target8; // Corresponds to "No. of Integrated Villages ready for Aadhar based authentication"

    @Column(name = "target9")
    private Integer target9; // Corresponds to "Percentage of villages where RoRs are made accessible through CSC, Kiosk, Online, etc"

    @Column(name = "target10")
    private Integer target10; // Corresponds to "Percentage of villages where digitally signed RoRs are issued"

    @Column(name = "target11")
    private Integer target11; // Corresponds to "Percentage of villages in which land records (RoRs) have been computerized"

}
