package com.matrix.bank.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.matrix.bank.domain.account.Account;
import com.matrix.bank.domain.transaction.Transaction;
import com.matrix.bank.domain.user.User;
import com.matrix.bank.util.CustomDateUtil;
import lombok.*;

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
    @NoArgsConstructor
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
    @NoArgsConstructor
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
        @NoArgsConstructor
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

    @Setter
    @Getter
    @NoArgsConstructor
    public static class AccountDepositRespDto {
        private Long id;                    // 계좌 id
        private Long number;                // 계좌번호
        private TransactionDto transaction;    // 트랜잭션 로그

        @Builder
        public AccountDepositRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.transaction = new TransactionDto(transaction);
        }

        @Setter
        @Getter
        @NoArgsConstructor
        public class TransactionDto {
            private Long id;
            private String classify;
            private String sender;
            private String receiver;
            private Long amount;
            private String tel;
            private String createdAt;
            @JsonIgnore
            private Long depositAccountBalance; // 내 계좌가 아니기 때문에 클라이언트에게 전달 X -> 서비스단에서 테스트 용도

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.classify = transaction.getClassify().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.tel = transaction.getTel();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    // DTO가 똑같아도 재사용하지 않기 (만약 출금할 때 뭔가 조금 DTO가 달라져야 하는데, DTO를 공유하고 있다면 수정 잘못하면 망한다
    // DTO를 독립적으로 만들어야 하는 이유
    @Setter
    @Getter
    @NoArgsConstructor
    public static class AccountWithdrawRespDto {
        private Long id;                    // 계좌 id
        private Long number;                // 계좌번호
        private Long balance;               // 계좌 잔액
        private TransactionDto transaction;    // 트랜잭션 로그

        @Builder
        public AccountWithdrawRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
            this.transaction = new TransactionDto(transaction);
        }

        @Setter
        @Getter
        @NoArgsConstructor
        public class TransactionDto {
            private Long id;
            private String classify;
            private String sender;
            private String receiver;
            private Long amount;
            private String createdAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.classify = transaction.getClassify().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class AccountTransferRespDto {
        private Long id;                    // 계좌 id
        private Long number;                // 계좌번호
        private Long balance;               // 출금 계좌 잔액
        private TransactionDto transaction;    // 트랜잭션 로그

        @Builder
        public AccountTransferRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
            this.transaction = new TransactionDto(transaction);
        }

        @Setter
        @Getter
        @NoArgsConstructor
        public class TransactionDto {
            private Long id;
            private String classify;
            private String sender;
            private String receiver;
            private Long amount;
//            @JsonIgnore // 테스트에서 입금 계좌 잔액을 확인하고 싶을 때 잠시 주석 풀고 확인한 후 다시 주석 처리
            private Long depositAccountBalance; // 입금 계좌 잔액은 외부에 노출시키면 안 된다.
            private String createdAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.classify = transaction.getClassify().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            }
        }
    }
}
