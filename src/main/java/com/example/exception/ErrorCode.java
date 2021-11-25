package com.example.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

//import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_ACCESS_TOKEN(BAD_REQUEST, "Access 토큰이 유효하지 않습니다."),
    INVALID_REFRESH_TOKEN(BAD_REQUEST, "Refresh 토큰이 유효하지 않습니다."),
    MISMATCH_REFRESH_TOKEN(BAD_REQUEST, "리프레시 토큰의 유저 정보가 일치하지 않습니다."),
    ALL_CHILD_TODO_NOT_COMPLETED(BAD_REQUEST, "하위 Todo들이 전부 완료되지 않습니다."),
    NOT_MISMATCH_ACCOUNT(BAD_REQUEST, "계정 아이디와 패스워드가 일치하지 않습니다."),
    INVALID_ARGUMENT(BAD_REQUEST, "입력 받은 요청이 타당하지 않습니다."),

    /* 401 UNAUTHORIZED : 인증 되지 않은 사용자 */
    INVALID_AUTH_TOKEN(UNAUTHORIZED, "권한 정보가 없는 토큰입니다"),
    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "현재 내 계정 정보가 존재하지 않습니다."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    MEMBER_NOT_FOUND(NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다."),
    TODO_NOT_FOUND(NOT_FOUND, "해당 Todo 정보를 찾을 수 없습니다."),
    WORKSPACE_NOT_FOUND(NOT_FOUND, "해당 작업 공간 정보를 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "로그아웃 된 사용자입니다."),
    PARTICIPANT_NOT_FOUND(NOT_FOUND, "작업 공간의 참가자 정보를 찾을 수 없습니다."),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(CONFLICT, "데이터가 이미 존재합니다."),

    /* 500 */
    NOT_FOUND_AUTHENTICATION_INFO(INTERNAL_SERVER_ERROR, "Authentication 정보를 찾을 수 없습니다.");

    ;

    private final HttpStatus httpStatus;
    private final String detail;
}
