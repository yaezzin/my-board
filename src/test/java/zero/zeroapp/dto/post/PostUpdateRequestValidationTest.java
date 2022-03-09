package zero.zeroapp.dto.post;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static zero.zeroapp.factory.dto.PostUpdateRequestFactory.createPostUpdateRequest;

class PostUpdateRequestValidationTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateTest() {
        // given
        PostUpdateRequest req = createPostUpdateRequest("title", "content", List.of(), List.of());

        // when
        Set<ConstraintViolation<PostUpdateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isEmpty();
    }

    @Test
    void invalidateByEmptyTitleTest() {
        // given
        String invalidValue = null;
        PostUpdateRequest req = createPostUpdateRequest(invalidValue, "content", List.of(), List.of());

        // when
        Set<ConstraintViolation<PostUpdateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankTitleTest() {
        // given
        String invalidValue = " ";
        PostUpdateRequest req = createPostUpdateRequest(invalidValue, "content", List.of(), List.of());

        // when
        Set<ConstraintViolation<PostUpdateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByEmptyContentTest() {
        // given
        String invalidValue = null;
        PostUpdateRequest req = createPostUpdateRequest("title", invalidValue, List.of(), List.of());

        // when
        Set<ConstraintViolation<PostUpdateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }

    @Test
    void invalidateByBlankContentTest() {
        // given
        String invalidValue = " ";
        PostUpdateRequest req = createPostUpdateRequest("title", invalidValue,  List.of(), List.of());

        // when
        Set<ConstraintViolation<PostUpdateRequest>> validate = validator.validate(req);

        // then
        assertThat(validate).isNotEmpty();
        assertThat(validate.stream().map(v -> v.getInvalidValue()).collect(toSet())).contains(invalidValue);
    }



}
