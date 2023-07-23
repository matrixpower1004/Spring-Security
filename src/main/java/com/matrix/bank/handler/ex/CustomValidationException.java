package com.matrix.bank.handler.ex;

import lombok.Getter;

import java.util.Map;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
@Getter
public class CustomValidationException extends RuntimeException {

    private Map<String, String> errorMap;

    public CustomValidationException(String message, Map<String, String> errorMap) {
        super(message);
        this.errorMap = errorMap;
    }
}