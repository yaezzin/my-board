package zero.zeroapp.config.security.guard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import zero.zeroapp.config.guard.AuthHelper;
import zero.zeroapp.entity.member.RoleType;
import zero.zeroapp.entity.post.Post;
import zero.zeroapp.exception.AccessDeniedException;
import zero.zeroapp.repository.post.PostRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostGuard {
    private final AuthHelper authHelper;
    private final PostRepository postRepository;

    public boolean check(Long id) {
        return authHelper.isAuthenticated() && hasAuthority(id);
    }

    private boolean hasAuthority(Long id) {
        return hasAdminRole() || isResourceOwner(id); // 1
    }

    private boolean isResourceOwner(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> { throw new AccessDeniedException(); });
        Long memberId = authHelper.extractMemberId();
        return post.getMember().getId().equals(memberId);
    }

    private boolean hasAdminRole() {
        return authHelper.extractMemberRoles().contains(RoleType.ADMIN);
    }
}