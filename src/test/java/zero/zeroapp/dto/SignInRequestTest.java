package zero.zeroapp.dto;

import org.junit.jupiter.api.Test;
import zero.zeroapp.dto.sign.SignInRequest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

class SignInRequestTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateTest() {
        // given
        SignInRequest request = createRequest();

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(request); // 검증 수행

        // then
        assertThat(validate).isEmpty(); // 제약 조건을 지키고 올바르게 검증되으면 응답 결과는 empty
    }

    @Test
    void invalidateByNotFormattedEmailTest() {
        // given
        String invalidValue = "email";
        SignInRequest req = createRequestWithEmail(invalidValue);

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        //제약 조건을 위반한 객체를 꺼내서, 위반된 값(given)을 가지고 있는지 확인
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByEmptyEmailTest() {
        // given
        String invalidValue = null;
        SignInRequest req = createRequestWithEmail(invalidValue);

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankEmailTest() {
        // given
        String invalidValue = " ";
        SignInRequest req = createRequestWithEmail(invalidValue);

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByEmptyPasswordTest() {
        // given
        String invalidValue = null;
        SignInRequest req = createRequestWithPassword(invalidValue);

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankPasswordTest() {
        // given
        String invalidValue = " ";
        SignInRequest req = createRequestWithPassword(" ");

        // when
        Set<ConstraintViolation<SignInRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    private SignInRequest createRequest() { //정상적인 요청 객체 생성
        return new SignInRequest("email@email.com", "123456a!");
    }

    private SignInRequest createRequestWithEmail(String email) { // email 객체가 잘못된 경우
        return new SignInRequest(email, "123456a!");
    }

    private SignInRequest createRequestWithPassword(String password) { // password 객체가 잘못된 경우
        return new SignInRequest("email@email.com", password);
    }
}