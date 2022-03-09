package zero.zeroapp.factory.dto;


import org.springframework.web.multipart.MultipartFile;
import zero.zeroapp.dto.post.PostUpdateRequest;

import java.util.List;

public class PostUpdateRequestFactory {
    public static PostUpdateRequest createPostUpdateRequest(String title, String content, List<MultipartFile> addedImages, List<Long> deletedImages) {
        return new PostUpdateRequest(title, content, addedImages, deletedImages);
    }
}
