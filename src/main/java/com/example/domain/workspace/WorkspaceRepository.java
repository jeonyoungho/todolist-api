package com.example.domain.workspace;

import com.example.domain.workspace.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long>, WorkspaceRepositoryCustom {

    @Query("select w from Workspace w join fetch w.participantGroup.participants p where w.id = :id")
    Workspace findByIdWithFetchJoinParticipants(@Param("id") Long id);
}
