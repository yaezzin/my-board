package zero.zeroapp.controller.post;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import zero.zeroapp.aop.AssignMemberId;
import zero.zeroapp.dto.post.PostCreateRequest;
import zero.zeroapp.dto.response.Response;
import zero.zeroapp.service.post.PostService;

import javax.validation.Valid;

@Api(value = "Post Controller", tags = "Post")
@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @ApiOperation(value = "게시글 생성", notes = "게시글을 생성한다.")
    @PostMapping("/api/posts")
    @ResponseStatus(HttpStatus.CREATED)
    @AssignMemberId
    public Response create(@Valid @ModelAttribute PostCreateRequest req) {
        return Response.success(postService.create(req));
    }

}
