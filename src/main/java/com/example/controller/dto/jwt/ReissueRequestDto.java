package com.example.controller.dto.jwt;

import lombok.Getter;

@Getter
public class ReissueRequestDto {
    private String accessToken;
    private String refreshToken;
}
