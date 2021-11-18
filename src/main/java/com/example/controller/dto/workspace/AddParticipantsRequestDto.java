package com.example.controller.dto.workspace;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
public class AddParticipantsRequestDto {
    @NotNull
    private Long workspaceId;

    @NotNull
    private List<Long> memberIds;

    @Builder
    public AddParticipantsRequestDto(Long workspaceId, List<Long> memberIds) {
        this.workspaceId = workspaceId;
        this.memberIds = memberIds;
    }
}
