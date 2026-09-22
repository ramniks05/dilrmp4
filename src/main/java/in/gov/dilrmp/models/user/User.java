package in.gov.dilrmp.models.user;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import in.gov.dilrmp.models.administrativeBoundry.UserLoginHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "muser")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User  {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "muser_id")
    @SequenceGenerator(name = "muser_id", sequenceName = "public.muser_seq", allocationSize = 1)
    private Long id;
    @Column(unique = true)
    private String userName;
    private String password;
    private boolean isActive;
    private String roleId;
    @Column(columnDefinition = "boolean default false")
    private boolean block;
    private Time blockTime;
    @Column(columnDefinition = "boolean default false")
    private Boolean active = false;
    @OneToOne(mappedBy = "muser", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile userProfile;
    @Transient
    private String newPassword;
    @Transient
    private String confirmPassword;

    private LocalDateTime lastPasswordChangeDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserLoginHistory> loginHistoryList = new ArrayList<>();




}
