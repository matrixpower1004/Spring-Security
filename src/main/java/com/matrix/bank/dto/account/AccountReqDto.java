package com.matrix.bank.dto.account;

import com.matrix.bank.domain.account.Account;
import com.matrix.bank.domain.user.User;
import lombok.*;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

/**
 * author         : Jason Lee
 * date           : 2023-07-25
 * description    :
 */
public class AccountReqDto {
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AccountSaveReqDto {
        @NotNull
        @Digits(integer = 4, fraction = 4) // 최대 4자
        private Long number;

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;
        // User는 세션에 있는 것으로 검증하기 때문에 받을 필요가 없다.

        public Account toEntity(User user) {
            return Account.builder()
                    .number(number)
                    .password(password)
                    .balance(1000L)
                    .user(user)
                    .build();
        }
    }
}
