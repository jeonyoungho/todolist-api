package com.example.controller.dto.workspace;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "참가자 추가 요청 DTO")
@Getter
@NoArgsConstructor
public class AddParticipantsRequestDto {
    @Schema(description = "작업 공간 고유 식별자", required = true)
    @NotNull
    private Long workspaceId;

    @Schema(description = "추가할 회원 고유 식별자 리스트", required = true)
    @NotNull
    private List<Long> memberIds;

    @Builder
    public AddParticipantsRequestDto(Long workspaceId, List<Long> memberIds) {
        this.workspaceId = workspaceId;
        this.memberIds = memberIds;
    }
}
