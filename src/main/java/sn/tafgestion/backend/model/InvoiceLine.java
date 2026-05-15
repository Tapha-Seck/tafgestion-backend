package sn.tafgestion.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_line")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "product_id")
    private Long productId;

    private String description;

    @Column(nullable = false)
    private BigDecimal quantity = BigDecimal.ONE;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "total_ht")
    private BigDecimal totalHt = BigDecimal.ZERO;
}