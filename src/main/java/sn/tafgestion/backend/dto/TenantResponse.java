package sn.tafgestion.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.tafgestion.backend.model.Tenant;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponse {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String country;
    private String currency;
    private Double tvaRate;
    private String invoicePrefix;
    private String primaryColor;
    private String logoUrl;
    private String schemaName;
    private String status;

    public static TenantResponse fromTenant(Tenant tenant) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .email(tenant.getEmail())
                .phone(tenant.getPhone())
                .country(tenant.getCountry())
                .currency(tenant.getCurrency())
                .tvaRate(tenant.getTvaRate())
                .invoicePrefix(tenant.getInvoicePrefix())
                .primaryColor(tenant.getPrimaryColor())
                .logoUrl(tenant.getLogoUrl())
                .schemaName(tenant.getSchemaName())
                .status(tenant.getStatus())
                .build();
    }
}
