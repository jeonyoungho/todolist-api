package com.example.domain.workspace;

import java.util.List;

public interface WorkspaceRepositoryCustom {
    List<Workspace> findAllByMemberId(Long memberId);
    Workspace findByIdWithFetchJoinParticipantAndMember(Long workspaceId);
}
