package zero.zeroapp.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zero.zeroapp.dto.member.MemberDto;
import zero.zeroapp.exception.MemberNotFoundException;
import zero.zeroapp.repository.member.MemberRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    //조회
    public MemberDto read(Long id) {
        return MemberDto.toDto(memberRepository.findById(id).orElseThrow(MemberNotFoundException::new));
    }

    //삭제
    @Transactional
    public void delete(Long id) {
        if(notExistsMember(id)) throw new MemberNotFoundException();
        memberRepository.deleteById(id);
    }

    private boolean notExistsMember(Long id) {
        return !memberRepository.existsById(id);
    }

}
