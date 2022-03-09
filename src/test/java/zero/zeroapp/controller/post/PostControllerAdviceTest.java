package zero.zeroapp.controller.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import zero.zeroapp.advice.ExceptionAdvice;
import zero.zeroapp.dto.post.PostCreateRequest;
import zero.zeroapp.exception.CategoryNotFoundException;
import zero.zeroapp.exception.MemberNotFoundException;
import zero.zeroapp.exception.PostNotFoundException;
import zero.zeroapp.exception.UnsupportedImageFormatException;
import zero.zeroapp.service.post.PostService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static zero.zeroapp.factory.dto.PostCreateRequestFactory.createPostCreateRequest;

@ExtendWith(MockitoExtension.class)
public class PostControllerAdviceTest {
    @InjectMocks PostController postController;
    @Mock PostService postService;
    MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).setControllerAdvice(new ExceptionAdvice()).build();
    }

    @Test
    void createExceptionByMemberNotFoundException() throws Exception{
        // given
        given(postService.create(any())).willThrow(MemberNotFoundException.class);

        // when, then
        performCreate()
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(-1007));
    }

    @Test
    void createExceptionByCategoryNotFoundException() throws Exception{
        // given
        given(postService.create(any())).willThrow(CategoryNotFoundException.class);

        // when, then
        performCreate()
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(-1010));
    }

    @Test
    void createExceptionByUnsupportedImageFormatException() throws Exception{
        // given
        given(postService.create(any())).willThrow(UnsupportedImageFormatException.class);

        // when, then
        performCreate()
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(-1013));
    }

    @Test
    void deleteExceptionByPostNotFoundTest() throws Exception {
        // given
        doThrow(PostNotFoundException.class).when(postService).delete(anyLong());

        // when, then
        mockMvc.perform(
                        delete("/api/posts/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(-1012));
    }

    private ResultActions performCreate() throws Exception {
        PostCreateRequest req = createPostCreateRequest();
        return mockMvc.perform(
                multipart("/api/posts")
                        .param("title", req.getTitle())
                        .param("content", req.getContent())
                        //.param("price", String.valueOf(req.getPrice()))
                        .param("categoryId", String.valueOf(req.getCategoryId()))
                        .with(requestPostProcessor -> {
                            requestPostProcessor.setMethod("POST");
                            return requestPostProcessor;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA));
    }
}
