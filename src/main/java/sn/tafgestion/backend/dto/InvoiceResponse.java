package sn.tafgestion.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.tafgestion.backend.model.Invoice;
import sn.tafgestion.backend.model.InvoiceLine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {

    private Long id;
    private String number;
    private Long clientId;
    private String clientName;
    private LocalDate date;
    private LocalDate dueDate;
    private String status;
    private String paymentMode;
    private BigDecimal subtotalHt;
    private BigDecimal tvaRate;
    private BigDecimal tvaAmount;
    private BigDecimal totalTtc;
    private String notes;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private List<LineResponse> lines;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineResponse {
        private Long id;
        private Long productId;
        private String description;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalHt;
    }

    public static InvoiceResponse fromInvoice(Invoice invoice) {
        List<LineResponse> lineResponses = null;
        if (invoice.getLines() != null) {
            lineResponses = invoice.getLines().stream()
                    .map(l -> LineResponse.builder()
                            .id(l.getId())
                            .productId(l.getProductId())
                            .description(l.getDescription())
                            .quantity(l.getQuantity())
                            .unitPrice(l.getUnitPrice())
                            .totalHt(l.getTotalHt())
                            .build())
                    .collect(Collectors.toList());
        }

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .number(invoice.getNumber())
                .clientId(invoice.getClientId())
                .date(invoice.getDate())
                .dueDate(invoice.getDueDate())
                .status(invoice.getStatus())
                .paymentMode(invoice.getPaymentMode())
                .subtotalHt(invoice.getSubtotalHt())
                .tvaRate(invoice.getTvaRate())
                .tvaAmount(invoice.getTvaAmount())
                .totalTtc(invoice.getTotalTtc())
                .notes(invoice.getNotes())
                .paidAt(invoice.getPaidAt())
                .createdAt(invoice.getCreatedAt())
                .lines(lineResponses)
                .build();
    }
}
