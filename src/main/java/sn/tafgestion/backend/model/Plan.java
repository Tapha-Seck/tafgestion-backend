package sn.tafgestion.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "billing_cycle", nullable = false)
    private String billingCycle;

    @Column(name = "max_users")
    private Integer maxUsers = 5;

    @Column(name = "max_products")
    private Integer maxProducts = 500;

    @Column(name = "max_clients")
    private Integer maxClients = 1000;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}