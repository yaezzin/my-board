package zero.zeroapp.factory.dto;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import zero.zeroapp.dto.post.PostCreateRequest;

import java.util.List;
/**
 * 생성자에 price 제거헀음(3번째)
 */

public class PostCreateRequestFactory {
    public static PostCreateRequest createPostCreateRequest() {
        return new PostCreateRequest("title", "content", 1L, 1L, List.of(
                new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "test2.PNG", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()),
                new MockMultipartFile("test3", "test3.PNG", MediaType.IMAGE_PNG_VALUE, "test3".getBytes())
        ));
    }

    public static PostCreateRequest createPostCreateRequest(String title, String content, /*Long price,*/ Long memberId, Long categoryId, List<MultipartFile> images) {
        return new PostCreateRequest(title, content, /*price, */ memberId, categoryId, images);
    }

    public static PostCreateRequest createPostCreateRequestWithTitle(String title) {
        return new PostCreateRequest(title, "content", 1L, 1L, List.of());
    }

    public static PostCreateRequest createPostCreateRequestWithContent(String content) {
        return new PostCreateRequest("title", content, 1L, 1L, List.of());
    }

    public static PostCreateRequest createPostCreateRequestWithPrice(Long price) {
        return new PostCreateRequest("title", "content", 1L, 1L, List.of());
    }

    public static PostCreateRequest createPostCreateRequestWithMemberId(Long memberId) {
        return new PostCreateRequest("title", "content", memberId, 1L, List.of());
    }

    public static PostCreateRequest createPostCreateRequestWithCategoryId(Long categoryId) {
        return new PostCreateRequest("title", "content", /*1000L,*/ 1L, categoryId, List.of());
    }

    public static PostCreateRequest createPostCreateRequestWithImages(List<MultipartFile> images) {
        return new PostCreateRequest("title", "content", 1L, 1L, images);
    }

}