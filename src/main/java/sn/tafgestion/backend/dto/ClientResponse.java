package sn.tafgestion.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.tafgestion.backend.model.Client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {

    private Long id;
    private String code;
    private String type;
    private String name;
    private String phone;
    private String email;
    private String city;
    private String address;
    private BigDecimal creditLimit;
    private boolean active;
    private LocalDateTime createdAt;

    public static ClientResponse fromClient(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .code(client.getCode())
                .type(client.getType())
                .name(client.getName())
                .phone(client.getPhone())
                .email(client.getEmail())
                .city(client.getCity())
                .address(client.getAddress())
                .creditLimit(client.getCreditLimit())
                .active(client.isActive())
                .createdAt(client.getCreatedAt())
                .build();
    }
}
