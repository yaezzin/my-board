package zero.zeroapp.repository.post;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import zero.zeroapp.dto.post.PostUpdateRequest;
import zero.zeroapp.entity.category.Category;
import zero.zeroapp.entity.member.Member;
import zero.zeroapp.entity.post.Image;
import zero.zeroapp.entity.post.Post;
import zero.zeroapp.exception.PostNotFoundException;
import zero.zeroapp.repository.catetory.CategoryRepository;
import zero.zeroapp.repository.member.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static zero.zeroapp.factory.dto.PostUpdateRequestFactory.createPostUpdateRequest;
import static zero.zeroapp.factory.entity.CategoryFactory.createCategory;
import static zero.zeroapp.factory.entity.ImageFactory.createImage;
import static zero.zeroapp.factory.entity.ImageFactory.createImageWithOriginName;
import static zero.zeroapp.factory.entity.MemberFactory.createMember;
import static zero.zeroapp.factory.entity.PostFactory.createPost;
import static zero.zeroapp.factory.entity.PostFactory.createPostWithImages;

@DataJpaTest
class PostRepositoryTest {
    @Autowired PostRepository postRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired ImageRepository imageRepository;
    @PersistenceContext EntityManager em;

    Member member;
    Category category;

    @BeforeEach
    void beforeEach() {
        member = memberRepository.save(createMember());
        category = categoryRepository.save(createCategory());
    }

    @Test
    void createAndReadTest() { // 생성 및 조회 검증
        // given
        Post post = postRepository.save(createPost(member, category));
        clear();

        // when
        Post foundPost = postRepository.findById(post.getId()).orElseThrow(PostNotFoundException::new);

        // then
        assertThat(foundPost.getId()).isEqualTo(post.getId());
        assertThat(foundPost.getTitle()).isEqualTo(post.getTitle());
    }

    @Test
    void deleteTest() { // 삭제 검증
        // given
        Post post = postRepository.save(createPost(member, category));
        clear();

        // when
        postRepository.deleteById(post.getId());
        clear();

        // then
        assertThatThrownBy(() -> postRepository.findById(post.getId()).orElseThrow(PostNotFoundException::new))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void createCascadeImageTest() { // 이미지도 연쇄적으로 생성되는지 검증
        // given
        Post post = postRepository.save(createPostWithImages(member, category, List.of(createImage(), createImage())));
        clear();

        // when
        Post foundPost = postRepository.findById(post.getId()).orElseThrow(PostNotFoundException::new);

        // then
        List<Image> images = foundPost.getImages();
        assertThat(images.size()).isEqualTo(2);
    }

    @Test
    void deleteCascadeImageTest() { // 이미지도 연쇄적으로 제거되는지 검증
        // given
        Post post = postRepository.save(createPostWithImages(member, category, List.of(createImage(), createImage())));
        clear();

        // when
        postRepository.deleteById(post.getId());
        clear();

        // then
        List<Image> images = imageRepository.findAll();
        assertThat(images.size()).isZero();
    }

    @Test
    void deleteCascadeByMemberTest() { // Member가 삭제되었을 때 연쇄적으로 Post도 삭제되는지 검증
        // given
        postRepository.save(createPostWithImages(member, category, List.of(createImage(), createImage())));
        clear();

        // when
        memberRepository.deleteById(member.getId());
        clear();

        // then
        List<Post> result = postRepository.findAll();
        Assertions.assertThat(result.size()).isZero();
    }

    @Test
    void deleteCascadeByCategoryTest() { // Category가 삭제되었을 때 연쇄적으로 Post도 삭제되는지 검증
        // given
        postRepository.save(createPostWithImages(member, category, List.of(createImage(), createImage())));
        clear();

        // when
        categoryRepository.deleteById(category.getId());
        clear();

        // then
        List<Post> result = postRepository.findAll();
        assertThat(result.size()).isZero();
    }

    @Test
    void findByIdWithMemberTest() {
        // given
        Post post = postRepository.save(createPost(member, category));

        // when
        Post foundPost = postRepository.findByIdWithMember(post.getId()).orElseThrow(PostNotFoundException::new);

        // then
        Member foundMember = foundPost.getMember();
        assertThat(foundMember.getEmail()).isEqualTo(member.getEmail());
    }


    @Test
    void updateTest() {
        // given
        Image a = createImageWithOriginName("a.jpg");
        Image b = createImageWithOriginName("b.png");
        Post post = postRepository.save(createPostWithImages(member, category, List.of(a, b)));
        clear();

        // when
        MockMultipartFile cFile = new MockMultipartFile("c", "c.png", MediaType.IMAGE_PNG_VALUE, "cFile".getBytes());
        PostUpdateRequest postUpdateRequest = createPostUpdateRequest("update title", "update content",  List.of(cFile), List.of(a.getId()));
        Post foundPost = postRepository.findById(post.getId()).orElseThrow(PostNotFoundException::new);
        foundPost.update(postUpdateRequest);
        clear();

        // then
        Post result = postRepository.findById(post.getId()).orElseThrow(PostNotFoundException::new);
        assertThat(result.getTitle()).isEqualTo(postUpdateRequest.getTitle());
        assertThat(result.getContent()).isEqualTo(postUpdateRequest.getContent());
        //assertThat(result.getPrice()).isEqualTo(postUpdateRequest.getPrice());
        List<Image> images = result.getImages();
        List<String> originNames = images.stream().map(i -> i.getOriginName()).collect(toList());
        assertThat(images.size()).isEqualTo(2);
        assertThat(originNames).contains(b.getOriginName(), cFile.getOriginalFilename());
        List<Image> resultImages = imageRepository.findAll();
        assertThat(resultImages.size()).isEqualTo(2);
    }

    void clear() {
        em.flush();
        em.clear();
    }
}
