package sn.tafgestion.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "client")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String type = "INDIVIDUAL";

    @Column(nullable = false)
    private String name;

    private String phone;
    private String email;
    private String city;
    private String address;

    @Column(name = "credit_limit")
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}