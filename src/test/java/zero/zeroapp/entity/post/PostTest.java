package zero.zeroapp.entity.post;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import zero.zeroapp.dto.post.PostUpdateRequest;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static zero.zeroapp.factory.dto.PostUpdateRequestFactory.createPostUpdateRequest;
import static zero.zeroapp.factory.entity.CategoryFactory.createCategory;
import static zero.zeroapp.factory.entity.ImageFactory.createImageWithIdAndOriginName;
import static zero.zeroapp.factory.entity.MemberFactory.createMember;
import static zero.zeroapp.factory.entity.PostFactory.createPostWithImages;


class PostTest {

    @Test
    void updateTest() {
        // given
        Image a = createImageWithIdAndOriginName(1L, "a.jpg");
        Image b = createImageWithIdAndOriginName(2L, "b.jpg");
        Post post = createPostWithImages(createMember(), createCategory(), List.of(a, b));

        // when
        MockMultipartFile cFile = new MockMultipartFile("c", "c.png", MediaType.IMAGE_PNG_VALUE, "cFile".getBytes());
        PostUpdateRequest postUpdateRequest = createPostUpdateRequest("update title", "update content", 1234L, List.of(cFile), List.of(a.getId()));
        Post.ImageUpdatedResult imageUpdatedResult = post.update(postUpdateRequest);

        // then
        assertThat(post.getTitle()).isEqualTo(postUpdateRequest.getTitle());
        assertThat(post.getContent()).isEqualTo(postUpdateRequest.getContent());
        assertThat(post.getPrice()).isEqualTo(postUpdateRequest.getPrice());

        List<Image> resultImages = post.getImages();
        List<String> resultOriginNames = resultImages.stream().map(i -> i.getOriginName()).collect(toList());
        assertThat(resultImages.size()).isEqualTo(2);
        assertThat(resultOriginNames).contains(b.getOriginName(), cFile.getOriginalFilename());

        List<MultipartFile> addedImageFiles = imageUpdatedResult.getAddedImageFiles();
        assertThat(addedImageFiles.size()).isEqualTo(1);
        assertThat(addedImageFiles.get(0).getOriginalFilename()).isEqualTo(cFile.getOriginalFilename());

        List<Image> addedImages = imageUpdatedResult.getAddedImages();
        List<String> addedOriginNames = addedImages.stream().map(i -> i.getOriginName()).collect(toList());
        assertThat(addedImages.size()).isEqualTo(1);
        assertThat(addedOriginNames).contains(cFile.getOriginalFilename());

        List<Image> deletedImages = imageUpdatedResult.getDeletedImages();
        List<String> deletedOriginNames = deletedImages.stream().map(i -> i.getOriginName()).collect(toList());
        assertThat(deletedImages.size()).isEqualTo(1);
        assertThat(deletedOriginNames).contains(a.getOriginName());
    }

}
