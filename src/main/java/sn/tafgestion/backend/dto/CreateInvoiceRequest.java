package sn.tafgestion.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateInvoiceRequest {

    @NotNull(message = "Client obligatoire")
    private Long clientId;

    @NotNull(message = "Date obligatoire")
    private LocalDate date;

    private LocalDate dueDate;
    private String notes;
    private BigDecimal tvaRate = new BigDecimal("18");

    @NotEmpty(message = "Au moins une ligne obligatoire")
    private List<InvoiceLineRequest> lines;

    @Data
    public static class InvoiceLineRequest {

        private Long productId;

        @NotNull(message = "Description obligatoire")
        private String description;

        @NotNull(message = "Quantité obligatoire")
        private BigDecimal quantity;

        @NotNull(message = "Prix unitaire obligatoire")
        private BigDecimal unitPrice;
    }
}
