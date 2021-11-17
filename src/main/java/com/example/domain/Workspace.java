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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_id")
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Embedded
    private ParticipantGroup participantGroup = new ParticipantGroup();

    @Builder
    public Workspace(String name, Participant... participants) {
        this.name = name;
        for(Participant participant : participants) {
            addParticipant(participant);
        }
    }

    //== 생성 메서드 ==//
    public static Workspace create(String name, Participant... participants) {
        return Workspace.builder()
                .name(name)
                .participants(participants)
                .build();
    }

    //== 변경 메서드 ==//
    public void addParticipant(Participant participant) {
        participantGroup.addParticipant(participant);
        participant.setWorkspace(this);
    }
}
