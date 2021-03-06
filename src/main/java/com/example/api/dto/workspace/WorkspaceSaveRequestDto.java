package com.example.api.dto.workspace;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "작업 공간 저장 요청 DTO")
@Getter
@NoArgsConstructor
public class WorkspaceSaveRequestDto {
    @Schema(description = "회원 고유 식별자", required = true)
    @NotNull
    private Long memberId;

    @Schema(description = "작업 공간 이름", required = true)
    @NotBlank
    private String workspaceName;

    @Builder
    public WorkspaceSaveRequestDto(Long memberId, String workspaceName) {
        this.memberId = memberId;
        this.workspaceName = workspaceName;
    }

    public static WorkspaceSaveRequestDto create(Long memberId, String workspaceName) {
        return WorkspaceSaveRequestDto.builder()
                .memberId(memberId)
                .workspaceName(workspaceName)
                .build();
    }

}
