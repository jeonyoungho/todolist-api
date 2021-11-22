package com.example.controller;

import com.example.controller.dto.user.UserListResponseDto;
import com.example.controller.dto.user.UserSignUpDto;
import com.example.service.UserService;
import com.example.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.version}")
public class UserController {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserService userService;

    @PostMapping("/user/signup")
    public ResponseEntity<String> signUp(@RequestBody final UserSignUpDto rq) {
        return userService.findByAccountId(rq.getAccountId()).isPresent()
                ? ResponseEntity.badRequest().build()
                : ResponseEntity.ok(TokenUtils.generateJwtToken(userService.signUp(rq)));
    }

    @GetMapping("/user/list")
    public ResponseEntity<UserListResponseDto> findAll() {
        final UserListResponseDto userListResponseDto = UserListResponseDto.builder()
                .userList(userService.findAll())
                .build();

        return ResponseEntity.ok(userListResponseDto);
    }
}
