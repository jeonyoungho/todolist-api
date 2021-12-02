package com.example.api.controller;

import com.example.api.dto.jwt.ReissueRequestDto;
import com.example.api.dto.jwt.TokenDto;
import com.example.api.dto.member.MemberListResponseDto;
import com.example.api.dto.member.MemberLoginRequestDto;
import com.example.api.dto.member.MemberSignUpRequestDto;
import com.example.api.service.MemberService;
import com.example.exception.ErrorDetails;
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
            @ApiResponse(responseCode = "409", description = "아이디 중복", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
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
            @ApiResponse(responseCode = "400", description = "아이디 패스워드 불일치", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @PostMapping("/member/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody MemberLoginRequestDto rq) {
        final TokenDto tokenDto = memberService.login(rq);
        return ResponseEntity.ok(tokenDto);
    }

    @Operation(summary = "로그아웃", description = "계정 아이디를 파라미터로 받아 로그아웃을 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근"),
    })
    @PostMapping("/member/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody String accountId) {
        memberService.logout(accountId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(summary = "Refresh 토큰 재발급", description = "Access 토큰과 Refresh 토큰을 파라미터로 받아 새로운 토큰을 재발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refresh 토큰 재발급 성공", content = @Content(schema = @Schema(implementation = TokenDto.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 토큰 정보(Access, Refresh)", content = @Content(schema = @Schema(implementation = TokenDto.class))),
    })
    @PostMapping("/member/reissue")
    public ResponseEntity<TokenDto> reissue(@Valid @RequestBody ReissueRequestDto rq) {
        final TokenDto tokenDto = memberService.reissue(rq);
        return ResponseEntity.ok(tokenDto);
    }

    @Operation(summary = "전체 회원 정보 리스트 조회", description = "전체 회원 정보 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 회원 정보 리스트 조회 성공", content = @Content(schema = @Schema(implementation = MemberListResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증이 안된 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
            @ApiResponse(responseCode = "403", description = "권한이 없는 접근 ", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
    })
    @GetMapping("/member/list")
    public ResponseEntity<MemberListResponseDto> findAll() {
        final MemberListResponseDto memberListResponseDto = memberService.findAll();
        return ResponseEntity.ok(memberListResponseDto);
    }
}
