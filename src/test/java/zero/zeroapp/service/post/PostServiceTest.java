package zero.zeroapp.service.post;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import zero.zeroapp.dto.post.PostCreateRequest;
import zero.zeroapp.dto.post.PostDto;
import zero.zeroapp.dto.post.PostUpdateRequest;
import zero.zeroapp.entity.post.Image;
import zero.zeroapp.entity.post.Post;
import zero.zeroapp.exception.CategoryNotFoundException;
import zero.zeroapp.exception.MemberNotFoundException;
import zero.zeroapp.exception.PostNotFoundException;
import zero.zeroapp.exception.UnsupportedImageFormatException;
import zero.zeroapp.repository.catetory.CategoryRepository;
import zero.zeroapp.repository.member.MemberRepository;
import zero.zeroapp.repository.post.PostRepository;
import zero.zeroapp.service.file.FileService;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static zero.zeroapp.factory.dto.PostCreateRequestFactory.createPostCreateRequest;
import static zero.zeroapp.factory.dto.PostCreateRequestFactory.createPostCreateRequestWithImages;
import static zero.zeroapp.factory.dto.PostUpdateRequestFactory.createPostUpdateRequest;
import static zero.zeroapp.factory.entity.CategoryFactory.createCategory;
import static zero.zeroapp.factory.entity.ImageFactory.createImage;
import static zero.zeroapp.factory.entity.ImageFactory.createImageWithIdAndOriginName;
import static zero.zeroapp.factory.entity.MemberFactory.createMember;
import static zero.zeroapp.factory.entity.PostFactory.createPostWithImages;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @InjectMocks PostService postService;
    @Mock PostRepository postRepository;
    @Mock MemberRepository memberRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock FileService fileService;

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

    @Test
    void readTest() {
        // given
        Post post = createPostWithImages(List.of(createImage(), createImage()));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        // when
        PostDto postDto = postService.read(1L);

        // then
        assertThat(postDto.getTitle()).isEqualTo(post.getTitle());
        assertThat(postDto.getImages().size()).isEqualTo(post.getImages().size());
    }

    @Test
    void readExceptionByPostNotFoundTest() {
        // given
        given(postRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        // when, then
        assertThatThrownBy(() -> postService.read(1L)).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void deleteTest() {
        // given
        Post post = createPostWithImages(List.of(createImage(), createImage()));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        // when
        postService.delete(1L);

        // then
        verify(fileService, times(post.getImages().size())).delete(anyString());
        verify(postRepository).delete(any());
    }

    @Test
    void deleteExceptionByNotFoundPostTest() {
        // given
        given(postRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        // when, then
        assertThatThrownBy(() -> postService.delete(1L)).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void updateTest() {
        // given
        Image a = createImageWithIdAndOriginName(1L, "a.png");
        Image b = createImageWithIdAndOriginName(2L, "b.png");
        Post post = createPostWithImages(List.of(a, b));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        MockMultipartFile cFile = new MockMultipartFile("c", "c.png", MediaType.IMAGE_PNG_VALUE, "c".getBytes());
        PostUpdateRequest postUpdateRequest = createPostUpdateRequest("title", "content",  List.of(cFile), List.of(a.getId()));

        // when
        postService.update(1L, postUpdateRequest);

        // then
        List<Image> images = post.getImages();
        List<String> originNames = images.stream().map(i -> i.getOriginName()).collect(toList());
        assertThat(originNames.size()).isEqualTo(2);
        assertThat(originNames).contains(b.getOriginName(), cFile.getOriginalFilename());

        verify(fileService, times(1)).upload(any(), anyString());
        verify(fileService, times(1)).delete(anyString());
    }

    @Test
    void updateExceptionByPostNotFoundTest() {
        // given
        given(postRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        // when, then
        assertThatThrownBy(() -> postService.update(1L, createPostUpdateRequest("title", "content", List.of(), List.of())))
                .isInstanceOf(PostNotFoundException.class);
    }
}