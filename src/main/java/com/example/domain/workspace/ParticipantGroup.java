package com.example.domain.workspace;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@ToString(of = {"participants"})
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Embeddable
public class ParticipantGroup {

    @OneToMany(mappedBy = "workspace", cascade = ALL)
    private List<Participant> participants = new ArrayList<>();

    public ParticipantGroup(List<Participant> participants) {
        this.participants = participants;
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    public void removeParticipant(Participant participant) {
        participants.remove(participant);
    }

}
