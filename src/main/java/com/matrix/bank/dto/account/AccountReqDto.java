package com.matrix.bank.dto.account;

import com.matrix.bank.domain.account.Account;
import com.matrix.bank.domain.user.User;
import lombok.*;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * author         : Jason Lee
 * date           : 2023-07-25
 * description    :
 */
public class AccountReqDto {
    @NoArgsConstructor
    @Setter
    @Getter
    public static class AccountSaveReqDto {
        @NotNull
        @Digits(integer = 4, fraction = 4) // 최대 4자
        private Long number;

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;
        // User는 세션에 있는 것으로 검증하기 때문에 받을 필요가 없다.

        @Builder
        public AccountSaveReqDto(Long number, Long password) {
            this.number = number;
            this.password = password;
        }

        public Account toEntity(User user) {
            return Account.builder()
                    .number(number)
                    .password(password)
                    .balance(1000L)
                    .user(user)
                    .build();
        }
    }

    @NoArgsConstructor
    @Setter
    @Getter
    public static class AccountDepositReqDto {
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;
        @NotNull
        private Long amount;
        @NotEmpty
        @Pattern(regexp = "DEPOSIT")
        private String classify; // DEPOSIT
        @NotEmpty
        @Pattern(regexp = "^[0-9]{9,12}")
        private String tel;

        @Builder
        public AccountDepositReqDto(Long number, Long amount, String classify, String tel) {
            this.number = number;
            this.amount = amount;
            this.classify = classify;
            this.tel = tel;
        }
    }

    @NoArgsConstructor
    @Setter
    @Getter
    public static class AccountWithdrawReqDto {
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;

        @NotNull
        private Long amount;

        @Pattern(regexp = "WITHDRAW")
        private String classify; // DEPOSIT

        @Builder
        public AccountWithdrawReqDto(Long number, Long password, Long amount, String classify) {
            this.number = number;
            this.password = password;
            this.amount = amount;
            this.classify = classify;
        }
    }
}
