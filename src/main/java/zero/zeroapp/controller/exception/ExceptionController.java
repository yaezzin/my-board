package zero.zeroapp.controller.exception;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import zero.zeroapp.exception.AccessDeniedException;
import zero.zeroapp.exception.AuthenticationEntryPointException;

@ApiIgnore
@RestController
public class ExceptionController {
    @GetMapping("/exception/entry-point")
    public void entryPoint() {
        throw new AuthenticationEntryPointException();
    }

    @GetMapping("/exception/access-denied")
    public void accessDenied() {
        throw new AccessDeniedException();
    }
}