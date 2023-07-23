package com.matrix.bank.handler.ex;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
public class CustomApiException extends RuntimeException {
    public CustomApiException(String message) {
        super(message);
    }
}
