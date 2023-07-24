package com.matrix.bank.handler.ex;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
// 추후 사용 예정
public class CustomForbiddenException extends RuntimeException {
    public CustomForbiddenException(String message) {
        super(message);
    }
}
