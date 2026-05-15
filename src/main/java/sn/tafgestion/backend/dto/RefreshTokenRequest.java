package sn.tafgestion.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token obligatoire")
    private String refreshToken;
}
