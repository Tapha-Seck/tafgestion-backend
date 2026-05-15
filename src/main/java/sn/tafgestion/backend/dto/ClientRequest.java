package sn.tafgestion.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClientRequest {

    private String code;

    private String type = "INDIVIDUAL";

    @NotBlank(message = "Nom du client obligatoire")
    private String name;

    private String phone;
    private String email;
    private String city;
    private String address;
    private BigDecimal creditLimit;
}
