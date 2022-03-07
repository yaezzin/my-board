package zero.zeroapp.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zero.zeroapp.dto.response.Response;
import zero.zeroapp.exception.*;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    // @ExceptionHandler에 예외 클래스를 지정하여, 해당 예외 발생 시 해당 메소드 수행
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response exception(Exception e) {
        log.info("e = {}", e.getMessage());
        return Response.failure(-1000, "오류가 발생하였습니다.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public Response methodArgumentNotValidException(MethodArgumentNotValidException e) { // 2
        return Response.failure(-1003, e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(LoginFailureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) //401
    public Response loginFailureException() {
        return Response.failure(-1004, "로그인에 실패하였습니다.");
    }

    @ExceptionHandler(MemberEmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT) //409
    public Response memberEmailAlreadyExistsException(MemberEmailAlreadyExistsException e) { // 4
        return Response.failure(-1005, e.getMessage() + "은 중복된 이메일 입니다.");
    }

    @ExceptionHandler(MemberNicknameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT) //409
    public Response memberNicknameAlreadyExistsException(MemberNicknameAlreadyExistsException e) { // 5
        return Response.failure(-1006, e.getMessage() + "은 중복된 닉네임 입니다.");
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) //404
    public Response memberNotFoundException() { // 6
        return Response.failure(-1007, "요청한 회원을 찾을 수 없습니다.");
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) //404
    public Response roleNotFoundException() { // 7
        return Response.failure(-1008, "요청한 권한 등급을 찾을 수 없습니다.");
    }

}
