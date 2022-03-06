package zero.zeroapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import zero.zeroapp.entity.member.Member;
import zero.zeroapp.entity.member.Role;

import java.util.List;

@Data
@AllArgsConstructor
public class SignUpRequest {

    private String email;
    private String password;
    private String username;
    private String nickname;

    public static Member toEntity(SignUpRequest request, Role role, PasswordEncoder encoder) {
        return new Member(request.email, encoder.encode(request.password),
                request.username, request.nickname, List.of(role));
    }
}
