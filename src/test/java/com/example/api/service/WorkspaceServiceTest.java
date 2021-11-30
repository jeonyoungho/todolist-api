package com.example.api.service;

import com.example.api.dto.member.MemberResponseDto;
import com.example.api.dto.workspace.AddParticipantsRequestDto;
import com.example.api.dto.workspace.WorkspaceResponseDto;
import com.example.api.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.workspace.Participant;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
import com.example.exception.CustomException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkspaceServiceTest {

    @InjectMocks
    WorkspaceService workspaceService;
    @Mock
    WorkspaceRepository workspaceRepository;
    @Mock
    MemberRepository memberRepository;

    private Member member;

    @Before
    public void setUp() {
        member = Member.create("test-id", "test-pw", "test-name", "test-city", "test-street", "test-zipcode", Authority.ROLE_USER);
        ReflectionTestUtils.setField(member, "id", 1L);
    }

    @Test
    public void saveWorkspace_ValidInput_Success() {
        // given
        WorkspaceSaveRequestDto rq = WorkspaceSaveRequestDto.create(member.getId(), "test-workspace-name");

        // mocking
        when(memberRepository.findById(rq.getUserId())).thenReturn(Optional.of(member));
        when(workspaceRepository.save(any(Workspace.class))).thenReturn(any());
        
        // when
        Long savedWorkspaceId = workspaceService.saveWorkspace(rq);

        // then
        verify(memberRepository).findById(rq.getUserId());
        verify(memberRepository, times(1)).findById(rq.getUserId());

        verify(workspaceRepository).save(any(Workspace.class));
        verify(workspaceRepository, times(1)).save(any(Workspace.class));
    }

    @Test(expected = CustomException.class)
    public void saveWorkspace_NotExistedMember_ThrowCustomException() {
        // given
        WorkspaceSaveRequestDto rq = WorkspaceSaveRequestDto.create(member.getId(), "test-workspace-name");

        // mocking
        when(memberRepository.findById(rq.getUserId())).thenReturn(Optional.empty());

        // when
        Long savedWorkspaceId = workspaceService.saveWorkspace(rq);

        // then
        fail("회원 조회시 예외가 발생해야 합니다.");
    }

    @Test
    public void addParticipants_ValidInput_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace-name", Participant.create(member));

        final Long workspaceId = 1L;
        List<Long> accountIds = Arrays.asList(new Long[]{new Long(1)});
        AddParticipantsRequestDto rq = AddParticipantsRequestDto.create(workspaceId, accountIds);

        // mocking
        when(workspaceRepository.findById(rq.getWorkspaceId())).thenReturn(Optional.of(workspace));
        when(memberRepository.findAllById(rq.getAccountIds())).thenReturn(anyList());

        // when
        workspaceService.addParticipants(rq);

        // then
        verify(workspaceRepository).findById(rq.getWorkspaceId());
        verify(workspaceRepository, times(1)).findById(rq.getWorkspaceId());

        verify(memberRepository).findAllById(rq.getAccountIds());
        verify(memberRepository, times(1)).findAllById(rq.getAccountIds());
    }

    @Test(expected = CustomException.class)
    public void addParticipants_NotExistedWorkspace_Success() {
        // given
        final Long workspaceId = 1L;
        List<Long> accountIds = Arrays.asList();
        AddParticipantsRequestDto rq = AddParticipantsRequestDto.create(workspaceId, accountIds);

        // mocking
        when(workspaceRepository.findById(rq.getWorkspaceId())).thenReturn(Optional.empty());

        // when
        workspaceService.addParticipants(rq);

        // then
        fail("작업 공간 조회시 예외가 발생해야 합니다.");
    }

    @Test
    public void findById_ValidInput_Success() {
        // given
        final String testWorkspaceName = "test-workspace-name";
        Workspace workspace = Workspace.create(testWorkspaceName, Participant.create(member));
        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));

        // when
        WorkspaceResponseDto result = workspaceService.findById(workspaceId);

        // then
        assertThat(result.getName()).isEqualTo(testWorkspaceName);

        verify(workspaceRepository).findById(workspaceId);
        verify(workspaceRepository, times(1)).findById(workspaceId);
    }

    @Test(expected = CustomException.class)
    public void findById_NotExistedWorkspace_ThrowCustomException() {
        // given
        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.empty());

        // when
        WorkspaceResponseDto result = workspaceService.findById(workspaceId);

        // then
        fail("작업 공간 조회시 예외가 발생해야 합니다.");
    }

    @Test
    public void findAllByMemberId_ValidInput_Success() {
        // given
        final Long memberId = 1L;

        // mocking
        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(workspaceRepository.findAllByMemberId(memberId)).thenReturn(anyList());

        // when
        List<WorkspaceResponseDto> result = workspaceService.findAllByMemberId(memberId);

        // then
        verify(memberRepository).existsById(memberId);
        verify(memberRepository, times(1)).existsById(memberId);

        verify(workspaceRepository).findAllByMemberId(memberId);
        verify(workspaceRepository, times(1)).findAllByMemberId(memberId);


    }

    @Test(expected = CustomException.class)
    public void findAllByMemberId_NotExistedMember_ThrowCustomException() {
        // given
        final Long memberId = 1L;

        // mocking
        when(memberRepository.existsById(memberId)).thenReturn(false);

        // when
        List<WorkspaceResponseDto> result = workspaceService.findAllByMemberId(memberId);

        // then
        fail("회원 조회시 예외가 발생해야 합니다.");
    }

    @Test
    public void findMembersById_ValidInput_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace-name", Participant.create(member));

        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.existsById(workspaceId)).thenReturn(true);
        when(workspaceRepository.findByIdWithFetchJoinParticipantAndMember(workspaceId)).thenReturn(workspace);

        // when
        List<MemberResponseDto> result = workspaceService.findMembersById(workspaceId);

        // then
        assertThat(result.get(0).getAccountId()).isEqualTo(member.getAccountId());

        verify(workspaceRepository).existsById(workspaceId);
        verify(workspaceRepository, times(1)).existsById(workspaceId);

        verify(workspaceRepository).findByIdWithFetchJoinParticipantAndMember(workspaceId);
        verify(workspaceRepository, times(1)).findByIdWithFetchJoinParticipantAndMember(workspaceId);
    }

    @Test(expected = CustomException.class)
    public void findMembersById_NotExistedWorkspace_ThrowCustomException() {
        // given
        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.existsById(workspaceId)).thenReturn(false);

        // when
        List<MemberResponseDto> result = workspaceService.findMembersById(workspaceId);

        // then
        fail("작업 공간 조회시 예외가 발생해야 합니다.");
    }

    @Test
    public void deleteById_ValidInput_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace-name", Participant.create(member));
        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));
        doNothing().when(workspaceRepository).delete(any(Workspace.class));

        // when
        workspaceService.deleteById(workspaceId);

        // then
        verify(workspaceRepository).findById(workspaceId);
        verify(workspaceRepository, times(1)).findById(workspaceId);

        verify(workspaceRepository).delete(any(Workspace.class));
        verify(workspaceRepository, times(1)).delete(any(Workspace.class));
    }

    @Test(expected = CustomException.class)
    public void deleteById_NotExistedWorkspace_ThrowCustomException() {
        // given
        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.empty());

        // when
        workspaceService.deleteById(workspaceId);

        // then
        fail("작업 공간 조회시 예외가 발생해야 합니다.");
    }

    @Test
    public void deleteParticipantByMemberId_ValidInput_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace-name", Participant.create(member));

        final Long memberId = 1L;
        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.findByIdWithFetchJoinParticipantAndMember(workspaceId)).thenReturn(workspace);

        // when
        workspaceService.deleteParticipantByMemberId(memberId, workspaceId);

        // then
        verify(workspaceRepository).findByIdWithFetchJoinParticipantAndMember(workspaceId);
        verify(workspaceRepository, times(1)).findByIdWithFetchJoinParticipantAndMember(workspaceId);
    }

    @Test(expected = CustomException.class)
    public void deleteParticipantByMemberId_NotExistedWorkspace_ThrowCustomException() {
        // given
        final Long memberId = 1L;
        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.findByIdWithFetchJoinParticipantAndMember(workspaceId)).thenReturn(null);

        // when
        workspaceService.deleteParticipantByMemberId(memberId, workspaceId);

        // then
        fail("작업 공간 조회시 예외가 발생해야 합니다.");
    }

    @Test(expected = CustomException.class)
    public void deleteParticipantByMemberId_NotExistedParticipant_ThrowCustomException() {
        // given
        Workspace workspace = Workspace.create("test-workspace-name", Participant.create(member));
        workspace.getParticipantGroup().removeParticipant(member.getId());

        final Long memberId = 1L;
        final Long workspaceId = 1L;

        // mocking
        when(workspaceRepository.findByIdWithFetchJoinParticipantAndMember(workspaceId)).thenReturn(workspace);

        // when
        workspaceService.deleteParticipantByMemberId(memberId, workspaceId);

        // then
        fail("입력 받은 회원이 작업 공간에 참가된 회원인지 확인시 예외가 발생해야 합니다.");
    }

}