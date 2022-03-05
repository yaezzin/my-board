package zero.zeroapp.repository.role;

import org.springframework.data.jpa.repository.JpaRepository;
import zero.zeroapp.entity.member.Role;
import zero.zeroapp.entity.member.RoleType;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleType(RoleType roleType);
}
