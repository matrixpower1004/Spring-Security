package com.matrix.bank.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author         : Jason Lee
 * date           : 2023-07-22
 * description    :
 */
@Getter
@RequiredArgsConstructor
public enum UserEnum {
    ADMIN("관리자"),
    CUSTOMER("고객");

    private final String value;
}
