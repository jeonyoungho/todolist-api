package com.example.api.dto.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class ReissueRequestDto {
    @Schema(description = "Access 토큰", required = true)
    @NotBlank
    private String accessToken;

    @Schema(description = "Refresh 토큰", required = true)
    @NotBlank
    private String refreshToken;

    public ReissueRequestDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
