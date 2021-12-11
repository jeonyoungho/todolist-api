package com.example.domain.workspace;

import java.util.List;
import java.util.Optional;

public interface WorkspaceRepositoryCustom {
    List<Workspace> findAllByMemberIdFetchJoinParticipant(Long memberId);
    Optional<Workspace> findByIdFetchJoinParticipantAndMember(Long workspaceId);
    Boolean existsByIdAndCurrentAccountId(Long workspaceId, String currentAccountId);
}
