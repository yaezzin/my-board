package zero.zeroapp.controller.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import zero.zeroapp.dto.post.PostCreateRequest;
import zero.zeroapp.dto.sign.SignInResponse;
import zero.zeroapp.entity.category.Category;
import zero.zeroapp.entity.member.Member;
import zero.zeroapp.entity.post.Post;
import zero.zeroapp.exception.MemberNotFoundException;
import zero.zeroapp.init.TestInitDB;
import zero.zeroapp.repository.catetory.CategoryRepository;
import zero.zeroapp.repository.member.MemberRepository;
import zero.zeroapp.repository.post.PostRepository;
import zero.zeroapp.service.post.PostService;
import zero.zeroapp.service.sign.SignService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static zero.zeroapp.factory.dto.PostCreateRequestFactory.createPostCreateRequest;
import static zero.zeroapp.factory.dto.SignInRequestFactory.createSignInRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
public class PostControllerIntegrationTest {
    @Autowired WebApplicationContext context;
    @Autowired MockMvc mockMvc;

    @Autowired TestInitDB initDB;
    @Autowired CategoryRepository categoryRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired SignService signService;
    @Autowired PostService postService;

    Member member1, member2, admin;
    Category category;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        initDB.initDB();
        member1 = memberRepository.findByEmail(initDB.getMember1Email()).orElseThrow(MemberNotFoundException::new);
        member2 = memberRepository.findByEmail(initDB.getMember2Email()).orElseThrow(MemberNotFoundException::new);
        admin = memberRepository.findByEmail(initDB.getAdminEmail()).orElseThrow(MemberNotFoundException::new);

        category = categoryRepository.findAll().get(0);
    }

    @Test
    void createTest() throws Exception {
        // given
        SignInResponse signInRes = signService.signIn(createSignInRequest(member1.getEmail(), initDB.getPassword()));
        PostCreateRequest req = createPostCreateRequest("title", "content", member1.getId(), category.getId(), List.of());

        // when, then
        mockMvc.perform(
                        multipart("/api/posts")
                                .param("title", req.getTitle())
                                .param("content", req.getContent())
                                //.param("price", String.valueOf(req.getPrice()))
                                .param("categoryId", String.valueOf(req.getCategoryId()))
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("POST");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .header("Authorization", signInRes.getAccessToken()))
                .andExpect(status().isCreated());

        Post post = postRepository.findAll().get(0);
        assertThat(post.getTitle()).isEqualTo("title");
        assertThat(post.getContent()).isEqualTo("content");
        assertThat(post.getMember().getId()).isEqualTo(member1.getId());
    }

    @Test
    void createUnauthorizedByNoneTokenTest() throws Exception {
        // given
        PostCreateRequest req = createPostCreateRequest("title", "content", member1.getId(), category.getId(), List.of());

        // when, then
        mockMvc.perform(
                        multipart("/api/posts")
                                .param("title", req.getTitle())
                                .param("content", req.getContent())
                                //.param("price", String.valueOf(req.getPrice()))
                                .param("categoryId", String.valueOf(req.getCategoryId()))
                                .with(requestPostProcessor -> {
                                    requestPostProcessor.setMethod("POST");
                                    return requestPostProcessor;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }


}
