package com.example.controller.dto.workspace;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class WorkspaceSaveRequestDto {
    @NotNull
    private Long memberId;

    @NotBlank
    private String name;

    @Builder
    public WorkspaceSaveRequestDto(Long memberId, String name) {
        this.memberId = memberId;
        this.name = name;
    }
}
