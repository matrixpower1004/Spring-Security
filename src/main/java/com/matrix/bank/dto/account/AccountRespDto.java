package com.matrix.bank.dto.account;

import com.matrix.bank.domain.account.Account;
import com.matrix.bank.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Getter
    @Setter
    public static class AccountListRespDto {
        private String fullname;
        private List<AccountDto> accounts = new ArrayList<>();

        public AccountListRespDto(User user, List<Account> accounts) {
            this.fullname = user.getFullname();
            this.accounts = accounts.stream().map(AccountDto::new).collect(Collectors.toList());
        }

        // 서비스에서 entity를 controller로 바로 넘기지 않고 Dto로 바꿔서 응답함.
        @Getter
        @Setter
        public class AccountDto {
            private Long id;
            private Long number;
            private Long balance;

            // Entity 객체를 Dto로 옮기는 작업
            // Entity를 Controller로 넘겨서 응답을 하게되면 json으로 변환하기 위해 메시지 컨버터가 발동한다.
            // 이 때 모든 필드를 getter를 다 때려서 내가 원하지 않는 lazy 로딩이 발생할 수 있다.

            public AccountDto(Account account) {
                this.id = account.getId();
                this.number = account.getNumber();
                this.balance = account.getBalance();
            }
        }
    }
}
