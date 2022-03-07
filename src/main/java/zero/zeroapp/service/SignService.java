package zero.zeroapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zero.zeroapp.dto.sign.SignInRequest;
import zero.zeroapp.dto.sign.SignInResponse;
import zero.zeroapp.dto.sign.SignUpRequest;
import zero.zeroapp.entity.member.Member;
import zero.zeroapp.entity.member.RoleType;
import zero.zeroapp.exception.LoginFailureException;
import zero.zeroapp.exception.MemberEmailAlreadyExistsException;
import zero.zeroapp.exception.MemberNicknameAlreadyExistsException;
import zero.zeroapp.exception.RoleNotFoundException;
import zero.zeroapp.repository.member.MemberRepository;
import zero.zeroapp.repository.role.RoleRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    // 가입
    @Transactional
    public void signUp(SignUpRequest request){
        // 중복 검사
        validateSingUpInfo(request);

        // 주어진 SignUpRequest를 Entity로 변경
        memberRepository.save(SignUpRequest.toEntity(
                request,
                roleRepository.findByRoleType(RoleType.USER).orElseThrow(RoleNotFoundException::new),
                passwordEncoder));
    }

    // 로그인 처리
    public SignInResponse signIn(SignInRequest req) {
        Member member = memberRepository.findByEmail(req.getEmail()).orElseThrow(LoginFailureException::new);
        validatePassword(req, member);

        String subject = createSubject(member);
        String accessToken = tokenService.createAccessToken(subject);
        String refreshToken = tokenService.createRefreshToken(subject);

        return new SignInResponse(accessToken, refreshToken);
    }

    private void validatePassword(SignInRequest req, Member member) {
        // 이메일과 비밀번호 중에 어떤 것에 의해 로그인에 실패했는지 알 수 없도록 동일하게 LoginFailureException 발생
        if(!passwordEncoder.matches(req.getPassword(), member.getPassword())) {
            throw new LoginFailureException();
        }
    }

    private String createSubject(Member member) {
        return String.valueOf(member.getId());
    }

    // 이메일, 닉네임 중복 검사 -> 예외 던짐
    private void validateSingUpInfo(SignUpRequest request) {
        if(memberRepository.existsByEmail(request.getEmail())) {
            throw new MemberEmailAlreadyExistsException(request.getEmail());
        }

        if(memberRepository.existsByNickname(request.getNickname())) {
            throw new MemberNicknameAlreadyExistsException(request.getNickname());
        }
    }

}
