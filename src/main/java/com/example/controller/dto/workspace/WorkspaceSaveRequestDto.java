package com.example.controller.dto.workspace;

import com.example.domain.Participant;
import com.example.domain.ParticipantGroup;
import com.example.domain.Workspace;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class WorkspaceSaveRequestDto {
    private Long memberId;
    private String name;
}
