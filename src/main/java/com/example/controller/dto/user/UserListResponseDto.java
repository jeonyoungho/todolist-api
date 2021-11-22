package com.example.controller.dto.user;

import com.example.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class UserListResponseDto {

    private final List<User> userList;

    @Builder
    public UserListResponseDto(List<User> userList) {
        this.userList = userList;
    }
}
