package zero.zeroapp.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL) // Null 값을 가지는 필드는 Json 응답에 포함하지 않음
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Response {

    private boolean success; // 요청 성공 여부
    private int code; // 응답 코드 (성공 시 0)
    private Result result; // 응답 데이터

    public static Response success() {
        return new Response(true, 0, null);
    }

    public static <T> Response success(T data) { //성공 시 응답 데이터도 반환
        return new Response(true, 0, new Success<>(data));
    }

    public static Response failure(int code, String msg) { // 실패 시 메시지도 반환
        return new Response(false, code, new Failure(msg));
    }

}
