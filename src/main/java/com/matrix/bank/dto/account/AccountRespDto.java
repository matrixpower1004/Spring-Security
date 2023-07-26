package com.matrix.bank.dto.account;

import com.matrix.bank.domain.account.Account;
import lombok.Getter;
import lombok.Setter;

/**
 * author         : Jason Lee
 * date           : 2023-07-25
 * description    :
 */
public class AccountRespDto {
    @Getter
    @Setter
    public static class AccountSaveRespDto {
        private Long id;
        private Long number;
        private Long balance;

        public AccountSaveRespDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
        }
    }
}
