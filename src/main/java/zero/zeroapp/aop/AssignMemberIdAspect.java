package zero.zeroapp.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import zero.zeroapp.config.guard.AuthHelper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@Aspect // Aspect 지정
@Component
@RequiredArgsConstructor
@Slf4j
public class AssignMemberIdAspect {

    private final AuthHelper authHelper;

    @Before("@annotation(zero.zeroapp.aop.AssignMemberId)") // 부가 기능이 수행되는 지점 지
    public void assignMemberId(JoinPoint joinPoint) { // @AssignMemberId가 붙은 메소드는 수행 직전 assignMemberId 메소드가 호출
        Arrays.stream(joinPoint.getArgs())
                .forEach(arg -> getMethod(arg.getClass(), "setMemberId")
                        .ifPresent(setMemberId -> invokeMethod(arg, setMemberId, authHelper.extractMemberId())));
    }

    private Optional<Method> getMethod(Class<?> clazz, String methodName) {
        try {
            return Optional.of(clazz.getMethod(methodName, Long.class));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    private void invokeMethod(Object obj, Method method, Object... args) { // 7
        try {
            method.invoke(obj, args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
