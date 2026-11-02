package in.gov.dilrmp.models.cna;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cna_lgd_link")
@Getter
@Setter
@NoArgsConstructor
public class CnaLgdLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cna_code", nullable = false, unique = true)
    private String cnaCode;

    @Column(name = "lgd_code", nullable = false)
    private Integer lgdCode;
}
