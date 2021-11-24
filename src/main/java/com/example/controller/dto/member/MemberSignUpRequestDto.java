package com.example.controller.dto.member;

import com.example.domain.member.Address;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotBlank;

@Getter
public class MemberSignUpRequestDto {
    @Schema(description = "회원 계정 아이디", maxLength = 20, required = true)
    @NotBlank
    private String accountId;

    @Schema(description = "회원 계정 패스워드", maxLength = 20, required = true)
    @NotBlank
    private String accountPw;

    @Schema(description = "회원 이름", maxLength = 20, required = true)
    @NotBlank
    private String name;

    @Schema(description = "회원 거주지 도시", maxLength = 20, required = true)
    @NotBlank
    private String city;

    @Schema(description = "회원 거주지 거리", maxLength = 20, required = true)
    @NotBlank
    private String street;

    @Schema(description = "회원 거주지 우편번호", maxLength = 20, required = true)
    @NotBlank
    private String zipcode;

    @Builder
    public MemberSignUpRequestDto(String accountId, String accountPw, String name, String city, String street, String zipcode) {
        this.accountId = accountId;
        this.accountPw = accountPw;
        this.name = name;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    public Member toEntity(BCryptPasswordEncoder passwordEncoder) {
        final Address address = Address.builder()
                .city(city)
                .street(street)
                .zipcode(zipcode)
                .build();

        final Member member = Member.builder()
                .accountId(accountId)
                .accountPw(passwordEncoder.encode(accountPw))
                .name(name)
                .address(address)
                .authority(Authority.ROLE_USER)
                .build();

        return member;
    }
}
