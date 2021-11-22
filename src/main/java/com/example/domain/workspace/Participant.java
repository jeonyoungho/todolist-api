package com.example.domain.workspace;

import com.example.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

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
    @JoinColumn(name = "member_id")
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @Builder
    public Participant(User user, Workspace workspace) {
        this.user = user;
        this.workspace = workspace;
    }

    //== 생성 메서드 ==//
    public static Participant create(User user) {
        return Participant.builder()
                .user(user)
                .build();
    }

    //== 연관관계 메서드 ==//
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
