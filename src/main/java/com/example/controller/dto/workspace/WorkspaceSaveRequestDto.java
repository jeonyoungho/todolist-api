package com.example.controller.dto.workspace;

import com.example.domain.Participant;
import com.example.domain.ParticipantGroup;
import com.example.domain.Workspace;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class WorkspaceSaveRequestDto {
    private String name;
    private ParticipantGroup participantGroup;
}
