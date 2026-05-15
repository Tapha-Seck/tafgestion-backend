package sn.tafgestion.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    private String reference;

    @NotBlank(message = "Nom du produit obligatoire")
    private String name;

    private String category;

    @NotNull(message = "Prix HT obligatoire")
    @PositiveOrZero(message = "Prix HT doit être positif")
    private BigDecimal priceHt;

    @PositiveOrZero(message = "Stock doit être positif")
    private Integer stock = 0;

    private String description;
}