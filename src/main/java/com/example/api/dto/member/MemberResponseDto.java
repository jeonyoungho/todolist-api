package com.example.api.dto.member;

import com.example.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

@Schema(description = "회원 정보 응답 DTO")
@Getter
@ToString
public class MemberResponseDto {
    @Schema(description = "회원 고유 식별자", required = true)
    private Long id;

    @Schema(description = "회원 계정 아이디", maxLength = 20, required = true)
    private String accountId;

    @Schema(description = "회원 계정 패스워드", maxLength = 20, required = true)
    private String accountPw;

    @Schema(description = "회원 이름", maxLength = 20, required = true)
    private String name;

    @Schema(description = "회원 거주지 도시", maxLength = 20, required = true)
    private String city;

    @Schema(description = "회원 거주지 거리", maxLength = 20, required = true)
    private String street;

    @Schema(description = "회원 거주지 우편번호", maxLength = 20, required = true)
    private String zipcode;

    public MemberResponseDto(Member entity) {
        this.id = entity.getId();
        this.accountId = entity.getAccountId();
        this.accountPw = entity.getAccountPw();
        this.name = entity.getName();
        this.city = entity.getAddress().getCity();
        this.street = entity.getAddress().getCity();
        this.zipcode = entity.getAddress().getZipcode();
    }
}
