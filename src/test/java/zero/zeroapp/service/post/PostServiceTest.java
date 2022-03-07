package zero.zeroapp.service.post;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import zero.zeroapp.dto.post.PostCreateRequest;
import zero.zeroapp.exception.CategoryNotFoundException;
import zero.zeroapp.exception.MemberNotFoundException;
import zero.zeroapp.exception.UnsupportedImageFormatException;
import zero.zeroapp.repository.catetory.CategoryRepository;
import zero.zeroapp.repository.member.MemberRepository;
import zero.zeroapp.repository.post.PostRepository;
import zero.zeroapp.service.file.FileService;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static zero.zeroapp.factory.dto.PostCreateRequestFactory.createPostCreateRequest;
import static zero.zeroapp.factory.dto.PostCreateRequestFactory.createPostCreateRequestWithImages;
import static zero.zeroapp.factory.entity.CategoryFactory.createCategory;
import static zero.zeroapp.factory.entity.ImageFactory.createImage;
import static zero.zeroapp.factory.entity.MemberFactory.createMember;
import static zero.zeroapp.factory.entity.PostFactory.createPostWithImages;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @InjectMocks PostService postService;
    @Mock
    PostRepository postRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    FileService fileService;

    @Test
    void createTest() {
        // given
        PostCreateRequest req = createPostCreateRequest();
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(createCategory()));
        given(postRepository.save(any())).willReturn(createPostWithImages(
                IntStream.range(0, req.getImages().size()).mapToObj(i -> createImage()).collect(toList()))
        );

        // when
        postService.create(req);

        // then
        verify(postRepository).save(any());
        verify(fileService, times(req.getImages().size())).upload(any(), anyString());
    }

    @Test
    void createExceptionByMemberNotFoundTest() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        // when, then
        assertThatThrownBy(() -> postService.create(createPostCreateRequest())).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void createExceptionByCategoryNotFoundTest() {
        // given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        // when, then
        assertThatThrownBy(() -> postService.create(createPostCreateRequest())).isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void createExceptionByUnsupportedImageFormatExceptionTest() {
        // given
        PostCreateRequest req = createPostCreateRequestWithImages(
                List.of(new MockMultipartFile("test", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes()))
        );
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(createMember()));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(createCategory()));

        // when, then
        assertThatThrownBy(() -> postService.create(req)).isInstanceOf(UnsupportedImageFormatException.class);
    }
}