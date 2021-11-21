package com.example.controller.dto.member;

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

    @Schema(description = "회원 계정 아이디", required = true)
    private String userId;

    @Schema(description = "회원 계정 패스워드", required = true)
    private String password;

    @Schema(description = "회원 이름", required = true)
    private String username;

    @Schema(description = "회원 거주 도시", required = true)
    private String city;

    @Schema(description = "회원 거주지 거리", required = true)
    private String street;

    @Schema(description = "회원 거주지 우편 번호", required = true)
    private String zipcode;

    public MemberResponseDto(Member entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.password = entity.getPassword();
        this.username = entity.getUsername();
        this.city = entity.getAddress().getCity();
        this.street = entity.getAddress().getCity();
        this.zipcode = entity.getAddress().getZipcode();
    }
}
