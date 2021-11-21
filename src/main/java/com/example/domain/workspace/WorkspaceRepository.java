package com.example.domain.workspace;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long>, WorkspaceRepositoryCustom {

}
