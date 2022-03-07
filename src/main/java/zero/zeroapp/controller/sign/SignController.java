package zero.zeroapp.controller.sign;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import zero.zeroapp.dto.response.Response;
import zero.zeroapp.dto.sign.SignInRequest;
import zero.zeroapp.dto.sign.SignUpRequest;
import zero.zeroapp.service.SignService;

import javax.validation.Valid;

import static zero.zeroapp.dto.response.Response.success;

@Api(value = "Sign Controller", tags = "Sign")
@RestController
@RequiredArgsConstructor
public class SignController {
    private final SignService signService;

    @ApiOperation(value = "회원가입", notes = "회원가입을 한다.")
    @PostMapping("/api/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public Response signUp(@Valid @RequestBody SignUpRequest request) {
        signService.signUp(request);
        return success();
    }

    @ApiOperation(value = "로그인", notes = "로그인을 한다.")
    @PostMapping("/api/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public Response signIn(@Valid @RequestBody SignInRequest request) {
        return success(signService.signIn(request));
    }

    @ApiOperation(value = "토큰 재발급", notes = "리프레시 토큰으로 새로운 액세스 토큰을 발급 받는다.")
    @PostMapping("/api/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public Response refreshToken(@ApiIgnore @RequestHeader(value = "Authorization") String refreshToken) {
        return success(signService.refreshToken(refreshToken));
    }

}
