package zero.zeroapp.config.security.guard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import zero.zeroapp.entity.member.RoleType;
import zero.zeroapp.repository.post.PostRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostGuard extends Guard {
    private final PostRepository postRepository;
    private List<RoleType> roleTypes = List.of(RoleType.ADMIN);

    @Override
    protected List<RoleType> getRoleTypes() {
        return roleTypes;
    }

    @Override
    protected boolean isResourceOwner(Long id) {
        return postRepository.findById(id)
                .map(post -> post.getMember())
                .map(member -> member.getId())
                .filter(memberId -> memberId.equals(AuthHelper.extractMemberId()))
                .isPresent();
    }
}