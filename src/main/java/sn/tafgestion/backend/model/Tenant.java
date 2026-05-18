package sn.tafgestion.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenant")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;
    private String address;

    @Column(nullable = false)
    private String country = "Sénégal";

    @Column(nullable = false)
    private String currency = "FCFA";

    @Column(name = "tva_rate")
    private Double tvaRate = 18.0;

    @Column(name = "invoice_prefix")
    private String invoicePrefix = "FAC";

    @Column(name = "invoice_footer")
    private String invoiceFooter;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "ninea")
    private String ninea;

    @Column(name = "rc")
    private String rc;

    @Column(name = "description")
    private String description;

    @Column(name = "website")
    private String website;

    @Column(name = "city")
    private String city;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "primary_color")
    private String primaryColor = "#1565C0";

    @Column(name = "schema_name", nullable = false, unique = true)
    private String schemaName;

    @Column(name = "plan_id")
    private Long planId;

    @Column(nullable = false)
    private String status = "ACTIVE";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}