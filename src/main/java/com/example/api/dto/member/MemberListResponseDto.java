package com.example.api.dto.member;

import com.example.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MemberListResponseDto {

    @Schema(description = "회원 정보 리스트")
    private final List<Member> members;

    @Builder
    public MemberListResponseDto(List<Member> members) {
        this.members = members;
    }
}
