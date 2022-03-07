package zero.zeroapp.controller.sign;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import zero.zeroapp.controller.response.Response;
import zero.zeroapp.dto.SignInRequest;
import zero.zeroapp.dto.SignUpRequest;
import zero.zeroapp.service.SignService;

import javax.validation.Valid;

import static zero.zeroapp.controller.response.Response.success;

@RestController
@RequiredArgsConstructor
public class SignController {
    private final SignService signService;

    @PostMapping("/api/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public Response signUp(@Valid @RequestBody SignUpRequest request) { // 2
        signService.signUp(request);
        return success();
    }

    @PostMapping("/api/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public Response signIn(@Valid @RequestBody SignInRequest request) { // 3
        return success(signService.signIn(request));
    }

}
