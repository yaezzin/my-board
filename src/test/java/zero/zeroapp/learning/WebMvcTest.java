package zero.zeroapp.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import zero.zeroapp.controller.response.Response;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class WebMvcTest {

    @InjectMocks TestController testController;
    MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(testController).build();
    }

    @Controller
    public static class TestController {
        @GetMapping("/test/ignore-null-value")
        public Response ignoreNullValueTest() {
            return Response.success();
        }
    }

    @Test
    void ignoreNullValueInJsonResponse() throws Exception {
        mockMvc.perform(
                get("/test/ignore-null-value"))
                .andExpect(status().isOk()) //응답 상태 코드 200
                .andExpect(jsonPath("$.result").doesNotExist()); //Json에 result 필드 X
    }

}
