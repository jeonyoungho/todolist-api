package com.example.domain.workspace;

import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@ToString(of = {"participants"})
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Embeddable
public class ParticipantGroup {

    @OneToMany(mappedBy = "workspace", cascade = ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    //== 연관관계 메서드 ==//
    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    public void removeParticipant(Long memberId) {
        Participant participant = findByMemberId(memberId);
        participants.remove(participant);
    }

    public int getSize() {
        return participants.size();
    }

    public Participant findByMemberId(Long memberId) {
        return participants.stream()
                .parallel()
                .filter(p -> p.getMember().getId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Boolean isExistByMemberId(Long memberId) {
        return participants.stream()
                .anyMatch(p -> memberId.equals(p.getMember().getId()));
    }

    public Boolean isExistByAccountId(String accountId) {
        return participants.stream()
                .anyMatch(p -> accountId.equals(p.getMember().getAccountId()));
    }
}
