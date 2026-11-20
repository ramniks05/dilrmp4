package in.gov.dilrmp.api.prayas.darpan;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pmdarpan_log")
@Getter
@Setter
@NoArgsConstructor
public class PrayasDarpanLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instance_code")
    private Integer instanceCode;

    @Column(name = "sec_code")
    private Integer secCode;

    @Column(name = "ministry_code")
    private Integer ministryCode;

    @Column(name = "dept_code")
    private Integer deptCode;

    @Column(name = "project_code")
    private Integer projectCode;

    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "req_start_dt")
    private LocalDateTime reqStartDt;

    @Column(name = "req_end_dt")
    private LocalDateTime reqEndDt;

    @Column(name = "msg", length = 1000)
    private String msg;

    @Column(name = "datadt_from")
    private LocalDate datadtFrom;

    @Column(name = "datadt_to")
    private LocalDate datadtTo;

    /** 1 = success, 0 = failure (DARPAN convention). */
    @Column(name = "status")
    private Integer status;

    @Column(name = "error_code")
    private Integer errorCode;

    @Column(name = "trigger_source", length = 40)
    private String triggerSource;
}
