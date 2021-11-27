package com.example.api.dto.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenDto {
    @Schema(description = "권한 타입")
    private String grantedType;

    @Schema(description = "Access 토큰")
    private String accessToken;

    @Schema(description = "Refresh 토큰")
    private String refreshToken;

    @Schema(description = "Access 토큰 만료시간")
    private Long accessTokenExpiresIn;

    @Builder
    public TokenDto(String grantedType, String accessToken, String refreshToken, Long accessTokenExpiresIn) {
        this.grantedType = grantedType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }
}
