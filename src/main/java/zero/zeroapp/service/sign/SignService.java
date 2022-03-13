package zero.zeroapp.service.sign;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zero.zeroapp.config.security.RefreshTokenFailureException;
import zero.zeroapp.config.token.TokenHelper;
import zero.zeroapp.dto.sign.RefreshTokenResponse;
import zero.zeroapp.dto.sign.SignInRequest;
import zero.zeroapp.dto.sign.SignInResponse;
import zero.zeroapp.dto.sign.SignUpRequest;
import zero.zeroapp.entity.member.Member;
import zero.zeroapp.entity.member.Role;
import zero.zeroapp.entity.member.RoleType;
import zero.zeroapp.exception.LoginFailureException;
import zero.zeroapp.exception.MemberEmailAlreadyExistsException;
import zero.zeroapp.exception.MemberNicknameAlreadyExistsException;
import zero.zeroapp.exception.RoleNotFoundException;
import zero.zeroapp.repository.member.MemberRepository;
import zero.zeroapp.repository.role.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenHelper accessTokenHelper;
    private final TokenHelper refreshTokenHelper;

    @Transactional
    public void signUp(SignUpRequest req) {
        validateSignUpInfo(req);
        String encodedPassword = passwordEncoder.encode(req.getPassword());
        List<Role> roles = List.of(roleRepository.findByRoleType(RoleType.USER).orElseThrow(RoleNotFoundException::new));
        memberRepository.save(
                new Member(req.getEmail(), encodedPassword, req.getUsername(), req.getNickname(), roles)
        );
    }

    @Transactional(readOnly = true)
    public SignInResponse signIn(SignInRequest req) {
        Member member = memberRepository.findWithRolesByEmail(req.getEmail()).orElseThrow(LoginFailureException::new);
        validatePassword(req, member);
        TokenHelper.PrivateClaims privateClaims = createPrivateClaims(member);
        String accessToken = accessTokenHelper.createToken(privateClaims);
        String refreshToken = refreshTokenHelper.createToken(privateClaims);
        return new SignInResponse(accessToken, refreshToken);
    }

    public RefreshTokenResponse refreshToken(String rToken) {
        TokenHelper.PrivateClaims privateClaims = refreshTokenHelper.parse(rToken).orElseThrow(RefreshTokenFailureException::new);
        String accessToken = accessTokenHelper.createToken(privateClaims);
        return new RefreshTokenResponse(accessToken);
    }

    private void validateSignUpInfo(SignUpRequest req) {
        if(memberRepository.existsByEmail(req.getEmail()))
            throw new MemberEmailAlreadyExistsException(req.getEmail());
        if(memberRepository.existsByNickname(req.getNickname()))
            throw new MemberNicknameAlreadyExistsException(req.getNickname());
    }

    private void validatePassword(SignInRequest req, Member member) {
        if(!passwordEncoder.matches(req.getPassword(), member.getPassword())) {
            throw new LoginFailureException();
        }
    }

    private TokenHelper.PrivateClaims createPrivateClaims(Member member) {
        return new TokenHelper.PrivateClaims(
                String.valueOf(member.getId()),
                member.getRoles().stream()
                        .map(memberRole -> memberRole.getRole())
                        .map(role -> role.getRoleType())
                        .map(roleType -> roleType.toString())
                        .collect(Collectors.toList()));
    }
}


/*
@Service
@RequiredArgsConstructor
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
    @Transactional(readOnly = true)
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

    public RefreshTokenResponse refreshToken(String rToken) {
        validateRefreshToken(rToken);
        String subject = tokenService.extractRefreshTokenSubject(rToken);
        String accessToken = tokenService.createAccessToken(subject);
        return new RefreshTokenResponse(accessToken);
    }

    private void validateRefreshToken(String rToken) {
        if(!tokenService.validateRefreshToken(rToken)) {
            throw new AuthenticationEntryPointException();
        }
    }
}
*/