package com.example.controller.dto.user;

import lombok.Getter;

@Getter
public class UserSignUpDto {
    private String accountId;
    private String accountPw;
    private String name;
    private String street;
    private String city;
    private String zipcode;
}
