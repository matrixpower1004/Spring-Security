package com.matrix.bank.domain.transaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * author         : Jason Lee
 * date           : 2023-07-22
 * description    :
 */
@Getter
@RequiredArgsConstructor
public enum TransactionEnum {
    WITHDRAW("출금"),
    DEPOSIT("입금"),
    TRANSFER("이체"),
    ALL("입출금내역");

    private final String value;
}