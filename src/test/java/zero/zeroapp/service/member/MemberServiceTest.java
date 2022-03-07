package zero.zeroapp.service.member;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zero.zeroapp.dto.member.MemberDto;
import zero.zeroapp.entity.member.Member;
import zero.zeroapp.exception.MemberNotFoundException;
import zero.zeroapp.repository.member.MemberRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks MemberService memberService;
    @Mock MemberRepository memberRepository;

    @Test
    void readTest() {
        Member member = new Member("email@email.com", "1728491!", "username", "nickname", List.of());
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        MemberDto result = memberService.read(1L);

        assertThat(result.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    void readExceptionByMemberNotFoundTest() {
        given(memberRepository.findById(any())).willReturn(Optional.ofNullable(null));

        assertThatThrownBy(() -> memberService.read(1L)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void deleteTest() {
        // given
        given(memberRepository.existsById(anyLong())).willReturn(true);

        // when
        memberService.delete(1L);

        // then
        verify(memberRepository).deleteById(anyLong());
    }

    @Test
    void deleteExceptionByMemberNotFoundTest() {
        // given
        given(memberRepository.existsById(anyLong())).willReturn(false);

        // when, then
        assertThatThrownBy(() -> memberService.delete(1L)).isInstanceOf(MemberNotFoundException.class);
    }

}