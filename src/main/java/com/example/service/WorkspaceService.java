package com.example.service;

import com.example.controller.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.ParticipantGroup;
import com.example.domain.Workspace;
import com.example.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public Long save(String name, ParticipantGroup...participantGroup) {


        return 1L;
    }
}
