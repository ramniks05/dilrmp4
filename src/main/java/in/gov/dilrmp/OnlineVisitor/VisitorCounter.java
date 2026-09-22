package in.gov.dilrmp.OnlineVisitor;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "visitor_counter")
@Data
public class VisitorCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_visitors")
    private Long totalVisitors;
}
