package com.matrix.bank.handler.aop;

import com.matrix.bank.handler.ex.CustomValidationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
@Component
@Aspect
public class CustomValidationAdvice {
    /**
     * 유효성 검사는 body data가 있는 경우에만 하면 된다. PUT, POST 등
     */

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping() {

    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMapping() {

    }

    // @Around 외에도 @Before 와 @After 도 있다.
    @Around("postMapping() || putMapping()")    // joinPoint의 전후 제어가 된다.
    public Object validationAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();  // joinPoint의 매개변수들
        for (Object arg : args) {
            if (arg instanceof BindingResult) {
                BindingResult bindingResult = (BindingResult) arg;

                if (bindingResult.hasErrors()) {
                    Map<String, String> errorMap = new HashMap<>();

                    for (FieldError error : bindingResult.getFieldErrors()) {
                        errorMap.put(error.getField(), error.getDefaultMessage());
                    }
                    throw new CustomValidationException("유효성 검사 실패", errorMap);
                }
                System.out.println("arg = " + arg);
            }
        }
        return proceedingJoinPoint.proceed(); // 정상적으로 해당 메서드를 실행해라!!
    }
}
