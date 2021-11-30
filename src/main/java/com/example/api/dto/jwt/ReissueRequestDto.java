package com.example.api.dto.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ReissueRequestDto {
    @Schema(description = "Access 토큰", required = true)
    @NotBlank
    private String accessToken;

    @Schema(description = "Refresh 토큰", required = true)
    @NotBlank
    private String refreshToken;


    @Builder
    public ReissueRequestDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
