package com.matrix.bank.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
@RequiredArgsConstructor
@Getter
public class ResponseDto<T> {
    // 응답 Dto는 한번 만들어지면 수정할 일이 없으므로 final로 선언
    private final Integer code; // 1 성공, -1 실패
    private final String message;
    private final T data;
}
