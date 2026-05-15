package sn.tafgestion.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTenantRequest {

    @NotBlank(message = "Nom obligatoire")
    private String name;

    @NotBlank(message = "Email obligatoire")
    @Email(message = "Email invalide")
    private String email;

    private String phone;
    private String address;
    private String country;
    private String currency;
    private Double tvaRate;
    private String invoicePrefix;
    private Long planId;

    // Compte admin initial
    private String adminEmail;
    private String adminPassword;
    private String adminFirstName;
    private String adminLastName;
}
