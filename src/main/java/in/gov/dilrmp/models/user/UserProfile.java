package in.gov.dilrmp.models.user;

import jakarta.persistence.*;

import lombok.*;
import java.time.LocalDate;

@Entity
@Table
@NoArgsConstructor @AllArgsConstructor
@Data
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_profile_id")
    @SequenceGenerator(name = "user_profile_id", sequenceName = "public.user_profile_seq", allocationSize = 1)
    private Long id;
    private String email;
    private String name;
    private String designation;
    private String officeAddress;
    private String officeName;
    private Boolean termAndCondition;
    @Column(name = "currentdate")
    private LocalDate currentDate;
    @OneToOne
    private User muser;
    @Transient
    private String fName;
    @Transient
    private String lName;
    @Transient
    private String captcha;
    private String mobileNo;

}
