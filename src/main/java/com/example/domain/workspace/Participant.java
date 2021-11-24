package com.example.domain.workspace;

import com.example.domain.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@ToString(of = {"id", "member"})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @Builder
    public Participant(Member member, Workspace workspace) {
        this.member = member;
        this.workspace = workspace;
    }

    //== 생성 메서드 ==//
    public static Participant create(Member member) {
        return Participant.builder()
                .member(member)
                .build();
    }

    //== 연관관계 메서드 ==//
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
