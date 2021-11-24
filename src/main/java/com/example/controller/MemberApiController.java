package com.example.controller;

import com.example.controller.dto.jwt.ReissueRequestDto;
import com.example.controller.dto.jwt.TokenDto;
import com.example.controller.dto.member.MemberListResponseDto;
import com.example.controller.dto.member.MemberLoginRequestDto;
import com.example.controller.dto.member.MemberSignUpRequestDto;
import com.example.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Tag(name = "Member", description = "회원 API")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "${api.version}")
public class MemberApiController {

    private final MemberService memberService;

    @Operation(summary = "회원 가입", description = "회원 정보를 파라미터로 받아 새로운 회원을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원 가입 성공", content = @Content(schema = @Schema(implementation = Long.class))),
    })
    @PostMapping("/member/signup")
    public ResponseEntity<Long> signUp(@Valid @RequestBody final MemberSignUpRequestDto rq) {
        final Long savedMemberId = memberService.signUp(rq);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedMemberId);
    }

    @Operation(summary = "로그인", description = "계정 아이디 및 패스워드를 파라미터로 받아 로그인을 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = TokenDto.class))),
    })
    @PostMapping("/member/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody MemberLoginRequestDto rq, HttpServletResponse response) {
        final TokenDto tokenDto = memberService.login(rq);
        return ResponseEntity.ok(tokenDto);
    }

    @Operation(summary = "로그아웃", description = "계정 아이디를 파라미터로 받아 로그아웃을 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
    })
    @PostMapping("/member/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody String accountId) {
        memberService.logout(accountId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Refresh Token 재발급", description = "Access Token과 Refresh Token을 파라미터로 받아 Token을 재발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refresh Token 재발급 성공", content = @Content(schema = @Schema(implementation = TokenDto.class))),
    })
    @PostMapping("/member/reissue")
    public ResponseEntity<TokenDto> reissue(@Valid @RequestBody ReissueRequestDto rq) {
        final TokenDto tokenDto = memberService.reissue(rq);
        return ResponseEntity.ok(tokenDto);
    }

    @Operation(summary = "전체 회원 정보 리스트 조회", description = "전체 회원 정보 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 회원 정보 리스트 조회 성공", content = @Content(schema = @Schema(implementation = MemberListResponseDto.class))),
    })
    @GetMapping("/member/list")
    public ResponseEntity<MemberListResponseDto> findAll() {
        final MemberListResponseDto memberListResponseDto = memberService.findAll();
        return ResponseEntity.ok(memberListResponseDto);
    }
}
