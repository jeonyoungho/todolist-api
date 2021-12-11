package com.example.api.service;

import com.example.api.dto.member.MemberResponseDto;
import com.example.api.dto.workspace.AddParticipantsRequestDto;
import com.example.api.dto.workspace.WorkspaceResponseDto;
import com.example.api.dto.workspace.WorkspaceSaveRequestDto;
import com.example.domain.member.Authority;
import com.example.domain.member.Member;
import com.example.domain.member.MemberRepository;
import com.example.domain.workspace.Workspace;
import com.example.domain.workspace.WorkspaceRepository;
import com.example.exception.CustomException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
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

        Collection<? extends GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(member.getAuthority().getValue()));
        UserDetails principal = new User(member.getAccountId(), "", authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", authorities));
    }

    @Test
    public void saveWorkspace_ValidInput_Success() {
        // given
        WorkspaceSaveRequestDto rq = WorkspaceSaveRequestDto.create(member.getId(), "test-workspace-name");

        // mocking
        when(memberRepository.findById(rq.getMemberId())).thenReturn(Optional.of(member));
        when(workspaceRepository.save(any(Workspace.class))).thenReturn(any(Workspace.class));

        // when
        Long savedWorkspaceId = workspaceService.saveWorkspace(rq);

        // then
        assertAll(
                () -> verify(memberRepository).findById(rq.getMemberId()),
                () -> verify(memberRepository, times(1)).findById(rq.getMemberId()),

                () -> verify(workspaceRepository).save(any(Workspace.class)),
                () -> verify(workspaceRepository, times(1)).save(any(Workspace.class))
        );
    }

    @Test(expected = CustomException.class)
    public void saveWorkspace_NotExistedMember_ThrowCustomException() {
        // given
        WorkspaceSaveRequestDto rq = WorkspaceSaveRequestDto.create(member.getId(), "test-workspace-name");

        // mocking
        when(memberRepository.findById(rq.getMemberId())).thenReturn(Optional.empty());

        // when
        Long savedWorkspaceId = workspaceService.saveWorkspace(rq);

        // then
        fail("회원 조회시 예외가 발생해야 합니다.");
    }

    @Test(expected = CustomException.class)
    public void saveWorkspace_InvalidRequestByClient_ThrowCustomException() {
        // given
        SecurityContextHolder.clearContext();

        WorkspaceSaveRequestDto rq = WorkspaceSaveRequestDto.create(member.getId(), "test-workspace-name");

        // mocking
        when(memberRepository.findById(rq.getMemberId())).thenReturn(Optional.of(member));

        // when
        Long savedWorkspaceId = workspaceService.saveWorkspace(rq);

        // then
        fail("작업 공간 등록 권한 체크시 예외가 발생해야 합니다.");
    }

    @Test
    public void addParticipants_ValidInput_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace-name", member);

        final Long workspaceId = anyLong();
        final List<Long> accountIds = Arrays.asList();
        AddParticipantsRequestDto rq = AddParticipantsRequestDto.create(workspaceId, accountIds);

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(rq.getWorkspaceId())).thenReturn(Optional.of(workspace));
        when(memberRepository.findAllById(rq.getAccountIds())).thenReturn(anyList());

        // when
        workspaceService.addParticipants(rq);

        // then
        assertAll(
                () -> verify(workspaceRepository).findByIdFetchJoinParticipantAndMember(rq.getWorkspaceId()),
                () -> verify(workspaceRepository, times(1)).findByIdFetchJoinParticipantAndMember(rq.getWorkspaceId()),

                () -> verify(memberRepository).findAllById(rq.getAccountIds()),
                () -> verify(memberRepository, times(1)).findAllById(rq.getAccountIds())
        );
    }

    @Test(expected = CustomException.class)
    public void addParticipants_NotExistedWorkspace_ThrowCustomException() {
        // given
        final Long fakeWorkspaceId = anyLong();
        AddParticipantsRequestDto rq = AddParticipantsRequestDto.builder()
                .workspaceId(fakeWorkspaceId)
                .build();

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(rq.getWorkspaceId())).thenReturn(Optional.empty());

        // when
        workspaceService.addParticipants(rq);

        // then
        fail("작업 공간 조회시 예외가 발생해야 합니다.");
    }

    @Test(expected = CustomException.class)
    public void addParticipants_InvalidRequestByClient_ThrowCustomException() {
        // given
        SecurityContextHolder.clearContext();

        Workspace workspace = Workspace.create("test-workspace-name", member);

        final Long workspaceId = anyLong();
        AddParticipantsRequestDto rq = AddParticipantsRequestDto.builder()
                .workspaceId(workspaceId)
                .build();

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(rq.getWorkspaceId())).thenReturn(Optional.of(workspace));

        // when
        workspaceService.addParticipants(rq);

        // then
        fail("참가자 리스트 추가에 대한 유효성 체크시 예외가 발생해야 합니다.");
    }

    @Test
    public void findById_ValidInput_Success() {
        // given
        final String testWorkspaceName = "test-workspace-name";
        Workspace workspace = Workspace.create(testWorkspaceName, member);
        final Long workspaceId = anyLong();

        // mocking
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));

        // when
        WorkspaceResponseDto result = workspaceService.findById(workspaceId);

        // then
        assertAll(
                () -> assertThat(result.getName()).isEqualTo(testWorkspaceName),

                () -> verify(workspaceRepository).findById(workspaceId),
                () -> verify(workspaceRepository, times(1)).findById(workspaceId)
        );
    }

    @Test(expected = CustomException.class)
    public void findById_NotExistedWorkspace_ThrowCustomException() {
        // given
        long fakeWorkspaceId = anyLong();

        // mocking
        when(workspaceRepository.findById(fakeWorkspaceId)).thenReturn(Optional.empty());

        // when
        WorkspaceResponseDto result = workspaceService.findById(fakeWorkspaceId);

        // then
        fail("작업 공간 조회시 예외가 발생해야 합니다.");
    }

    @Test
    public void findAllByMemberId_ValidInput_Success() {
        // given
        final Long memberId = anyLong();

        // mocking
        when(workspaceRepository.findAllByMemberIdFetchJoinParticipant(memberId)).thenReturn(anyList());

        // when
        List<WorkspaceResponseDto> result = workspaceService.findAllByMemberId(memberId);

        // then
        assertAll(
                () -> verify(workspaceRepository).findAllByMemberIdFetchJoinParticipant(memberId),
                () -> verify(workspaceRepository, times(1)).findAllByMemberIdFetchJoinParticipant(memberId)
        );

    }

    @Test
    public void findAllByMemberId_NotExistedMember_EmptyList() {
        // given
        final Long fakeMemberId = anyLong();

        // mocking
        when(workspaceRepository.findAllByMemberIdFetchJoinParticipant(fakeMemberId)).thenReturn(anyList());

        // when
        List<WorkspaceResponseDto> result = workspaceService.findAllByMemberId(fakeMemberId);

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.size()).isEqualTo(0)
        );
    }

    @Test
    public void findMembersById_ValidInput_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace-name", member);
        final Long workspaceId = anyLong();

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId)).thenReturn(Optional.of(workspace));

        // when
        List<MemberResponseDto> result = workspaceService.findMembersById(workspaceId);

        // then
        assertAll(
                () -> assertThat(result.get(0).getAccountId()).isEqualTo(member.getAccountId()),

                () -> verify(workspaceRepository).findByIdFetchJoinParticipantAndMember(workspaceId),
                () -> verify(workspaceRepository, times(1)).findByIdFetchJoinParticipantAndMember(workspaceId)
        );
    }

    @Test(expected = CustomException.class)
    public void findMembersById_NotExistedWorkspace_ThrowCustomException() {
        // given
        final Long fakeWorkspaceId = anyLong();

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(fakeWorkspaceId)).thenReturn(Optional.empty());

        // when
        List<MemberResponseDto> result = workspaceService.findMembersById(fakeWorkspaceId);

        // then
        fail("작업 공간 조회시 예외가 발생해야 합니다.");
    }

    @Test
    public void deleteById_ValidInput_Success() {
        // given
        Workspace workspace = Workspace.create("test-workspace-name", member);
        final Long workspaceId = anyLong();

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId)).thenReturn(Optional.of(workspace));
        doNothing().when(workspaceRepository).delete(any(Workspace.class));

        // when
        workspaceService.deleteById(workspaceId);

        // then
        assertAll(
                () -> verify(workspaceRepository).findByIdFetchJoinParticipantAndMember(workspaceId),
                () -> verify(workspaceRepository, times(1)).findByIdFetchJoinParticipantAndMember(workspaceId),

                () -> verify(workspaceRepository).delete(any(Workspace.class)),
                () -> verify(workspaceRepository, times(1)).delete(any(Workspace.class))
        );
    }

    @Test(expected = CustomException.class)
    public void deleteById_NotExistedWorkspace_ThrowCustomException() {
        // given
        final Long fakeWorkspaceId = anyLong();

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(fakeWorkspaceId)).thenReturn(Optional.empty());

        // when
        workspaceService.deleteById(fakeWorkspaceId);

        // then
        fail("작업 공간 조회시 예외가 발생해야 합니다.");
    }

    @Test(expected = CustomException.class)
    public void deleteById_InvalidRequestByClient_ThrowCustomException() {
        // given
        SecurityContextHolder.clearContext();

        Workspace workspace = Workspace.create("test-workspace-name", member);
        final Long fakeWorkspaceId = anyLong();

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(fakeWorkspaceId)).thenReturn(Optional.of(workspace));

        // when
        workspaceService.deleteById(fakeWorkspaceId);

        // then
        fail("작업 공간 삭제 유효성 체크시 예외가 발생해야 합니다.");
    }

    @Test
    public void deleteParticipantByMemberId_ValidInput_Success() {
        // given
        final Long memberId = 1L;
        Workspace workspace = Workspace.create("test-workspace-name", member);
        ReflectionTestUtils.setField(member, "id", memberId);

        final Long workspaceId = anyLong();

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId)).thenReturn(Optional.of(workspace));

        // when
        workspaceService.deleteParticipantByMemberId(memberId, workspaceId);

        // then
        assertAll(
                () -> verify(workspaceRepository).findByIdFetchJoinParticipantAndMember(workspaceId),
                () -> verify(workspaceRepository, times(1)).findByIdFetchJoinParticipantAndMember(workspaceId)
        );
    }

    @Test(expected = CustomException.class)
    public void deleteParticipantByMemberId_NotExistedWorkspace_ThrowCustomException() {
        // given
        final Long memberId = 1L;
        final Long workspaceId = anyLong();

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId)).thenReturn(Optional.empty());

        // when
        workspaceService.deleteParticipantByMemberId(memberId, workspaceId);

        // then
        fail("작업 공간 조회시 예외가 발생해야 합니다.");
    }

    @Test(expected = CustomException.class)
    public void deleteParticipantByMemberId_InvalidRequestByClient_ThrowCustomException() {
        // given
        SecurityContextHolder.clearContext();
        Workspace workspace = Workspace.create("test-workspace-name", member);

        final Long memberId = 1L;
        final Long workspaceId = anyLong();

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId)).thenReturn(Optional.of(workspace));

        // when
        workspaceService.deleteParticipantByMemberId(memberId, workspaceId);

        // then
        fail("참가자 삭제 유효성 체크시 예외가 발생해야 합니다.");
    }

    @Test(expected = CustomException.class)
    public void deleteParticipantByMemberId_NotExistedParticipant_ThrowCustomException() {
        // given
        Workspace workspace = Workspace.create("test-workspace-name", member);
        workspace.removeParticipant(member.getId());

        final Long memberId = 1L;
        final Long workspaceId = anyLong();

        // mocking
        when(workspaceRepository.findByIdFetchJoinParticipantAndMember(workspaceId)).thenReturn(Optional.of(workspace));

        // when
        workspaceService.deleteParticipantByMemberId(memberId, workspaceId);

        // then
        fail("입력 받은 회원이 작업 공간에 참가된 회원인지 확인시 예외가 발생해야 합니다.");
    }

}
