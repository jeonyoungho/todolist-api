package com.example.controller.dto.member;

import com.example.domain.member.Address;
import com.example.domain.member.Member;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@ToString
public class MemberResponseDto {
    private Long id;
    private String userId;
    private String password;
    private String username;
    private String city;
    private String street;
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
