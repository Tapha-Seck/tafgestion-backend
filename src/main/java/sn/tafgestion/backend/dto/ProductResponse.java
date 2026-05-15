package sn.tafgestion.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.tafgestion.backend.model.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String reference;
    private String name;
    private String category;
    private BigDecimal priceHt;
    private Integer stock;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;

    public static ProductResponse fromProduct(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .reference(product.getReference())
                .name(product.getName())
                .category(product.getCategory())
                .priceHt(product.getPriceHt())
                .stock(product.getStock())
                .description(product.getDescription())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
