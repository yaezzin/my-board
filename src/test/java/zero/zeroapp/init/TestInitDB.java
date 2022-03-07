package zero.zeroapp.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import zero.zeroapp.entity.category.Category;
import zero.zeroapp.entity.member.Member;
import zero.zeroapp.entity.member.Role;
import zero.zeroapp.entity.member.RoleType;
import zero.zeroapp.exception.RoleNotFoundException;
import zero.zeroapp.repository.catetory.CategoryRepository;
import zero.zeroapp.repository.member.MemberRepository;
import zero.zeroapp.repository.role.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TestInitDB {
    @Autowired RoleRepository roleRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired CategoryRepository categoryRepository;

    private String adminEmail = "admin@admin.com";
    private String member1Email = "member1@member.com";
    private String member2Email = "member2@member.com";
    private String password = "123456a!";

    @Transactional
    public void initDB() {
        initRole();
        initTestAdmin();
        initTestMember();
        initCategory();
    }

    private void initRole() {
        roleRepository.saveAll(
                List.of(RoleType.values()).stream().map(roleType -> new Role(roleType)).collect(Collectors.toList())
        );
    }

    private void initTestAdmin() {
        memberRepository.save(
                new Member(adminEmail, passwordEncoder.encode(password), "admin", "admin",
                        List.of(roleRepository.findByRoleType(RoleType.USER).orElseThrow(RoleNotFoundException::new),
                                roleRepository.findByRoleType(RoleType.ADMIN).orElseThrow(RoleNotFoundException::new)))
        );
    }

    private void initTestMember() {
        memberRepository.saveAll(
                List.of(
                        new Member(member1Email, passwordEncoder.encode(password), "member1", "member1",
                                List.of(roleRepository.findByRoleType(RoleType.USER).orElseThrow(RoleNotFoundException::new))),
                        new Member(member2Email, passwordEncoder.encode(password), "member2", "member2",
                                List.of(roleRepository.findByRoleType(RoleType.USER).orElseThrow(RoleNotFoundException::new))))
        );
    }

    private void initCategory() {
        Category category1 = new Category("category1", null);
        Category category2 = new Category("category2", category1);
        categoryRepository.saveAll(List.of(category1, category2));
    }


    public String getAdminEmail() {
        return adminEmail;
    }

    public String getMember1Email() {
        return member1Email;
    }

    public String getMember2Email() {
        return member2Email;
    }

    public String getPassword() {
        return password;
    }
}
