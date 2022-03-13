package zero.zeroapp.config.security.guard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import zero.zeroapp.entity.member.RoleType;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class MemberGuard extends Guard {
    private List<RoleType> roleTypes = List.of(RoleType.ADMIN);

    @Override
    protected List<RoleType> getRoleTypes() {
        return roleTypes;
    }

    @Override
    protected boolean isResourceOwner(Long id) {
        return id.equals(AuthHelper.extractMemberId());
    }
}