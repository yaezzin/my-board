package zero.zeroapp.config.security.guard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import zero.zeroapp.entity.member.RoleType;
import zero.zeroapp.entity.post.Post;
import zero.zeroapp.exception.AccessDeniedException;
import zero.zeroapp.repository.post.PostRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostGuard extends Guard {
    private final PostRepository postRepository;
    private List<RoleType> roleTypes = List.of(RoleType.ADMIN);

    @Override
    protected List<RoleType> getRoleTypes() {
        return roleTypes;
    }

    @Override
    protected boolean isResourceOwner(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> { throw new AccessDeniedException();});
        Long memberId = AuthHelper.extractMemberId();
        return post.getMember().getId().equals(memberId);
    }
}