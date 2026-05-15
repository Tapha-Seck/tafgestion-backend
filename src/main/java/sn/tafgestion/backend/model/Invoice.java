package sn.tafgestion.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(name = "client_id")
    private Long clientId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "payment_mode")
    private String paymentMode;

    @Column(name = "subtotal_ht")
    private BigDecimal subtotalHt = BigDecimal.ZERO;

    @Column(name = "tva_rate")
    private BigDecimal tvaRate = new BigDecimal("18");

    @Column(name = "tva_amount")
    private BigDecimal tvaAmount = BigDecimal.ZERO;

    @Column(name = "total_ttc")
    private BigDecimal totalTtc = BigDecimal.ZERO;

    private String notes;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "invoiceId",
            cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<InvoiceLine> lines;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
