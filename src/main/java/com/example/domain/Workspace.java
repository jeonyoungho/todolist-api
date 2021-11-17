package com.example.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "workspace_id")
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Embedded
    private ParticipantGroup participantGroup;

    @Builder
    public Workspace(String name, ParticipantGroup participantGroup) {
        this.name = name;
        this.participantGroup = participantGroup;
    }

    //== 생성 메서드 ==//
    public Workspace create(String name, Participant... participants) {
        Workspace workspace = Workspace.builder()
                .name(name)
                .build();

        for(Participant participant : participants) {
            workspace.participantGroup.addParticipant(participant);
        }

        return workspace;
    }
}
