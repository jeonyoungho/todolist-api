package com.example.controller.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.NotBlank;

@Getter
public class MemberLoginRequestDto {
    @Schema(description = "회원 계정 아이디", maxLength = 20, required = true)
    @NotBlank
    private String accountId;

    @Schema(description = "회원 계정 패스워드", maxLength = 20, required = true)
    @NotBlank
    private String accountPw;

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(accountId, accountPw);
    }
}
