package com.matrix.bank.dto.transaction;

import com.matrix.bank.domain.account.Account;
import com.matrix.bank.domain.transaction.Transaction;
import com.matrix.bank.util.CustomDateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * author         : Jason Lee
 * date           : 2023-07-29
 * description    :
 */
public class TransactionRespDto {

    @NoArgsConstructor
    @Setter
    @Getter
    public static class TransactionListRespDto {
        private List<TransactionDto> transactions = new ArrayList<>();

        public TransactionListRespDto(List<Transaction> transactions, Account account) {
            this.transactions = transactions.stream()
                    .map(transaction -> new TransactionDto(transaction, account.getNumber()))
                    .collect(Collectors.toList());
        }

        @NoArgsConstructor
        @Setter
        @Getter
        public class TransactionDto {
            private Long id;
            private String classify;
            private Long amount;
            private String sender;
            private String receiver;
            private String tel;
            private String createdAt;
            private Long balance;

            public TransactionDto(Transaction transaction, Long accountNumber) {
                this.id = transaction.getId();
                this.classify = transaction.getClassify().getValue();
                this.amount = transaction.getAmount();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
                this.tel = transaction.getTel() == null ? "없음" : transaction.getTel();

                // 이런 방식이 복잡하면 Dto를 따로 만들어서 사용하면 된다.
                // 현재는 동적쿼리로 인해 동적으로 변하는 Dto를 만드는 것.
                // 반대로 (츨금계좌 = 값, 입금계좌 = null) -> ATM에서 출금만 한 경우로 출금계좌의 잔액을 봐야 한다.
                if (transaction.getDepositAccount() == null) {
                    this.balance = transaction.getWithdrawAccountBalance();
                } else if (transaction.getWithdrawAccount() == null) { // 출금계좌는 없고 입금만 존재하는 경우
                    // 1111 계좌의 입출금 내역 (출금계좌 = null, 입금계좌 = 값) -> ATM에서 입금만 한 경우로 입금계좌의 잔액을 봐야 한다.
                    this.balance = transaction.getDepositAccountBalance();
                } else { // 1111 계좌의 입출금 내역 조회 (출금계좌 = 값, 입금계좌 = 값) -> 계좌이체
                    if (accountNumber.longValue() == transaction.getDepositAccount().getNumber().longValue()) {
                        this.balance = transaction.getDepositAccountBalance();
                    } else {
                        this.balance = transaction.getWithdrawAccountBalance();
                    }
                }
            }
        }
    }
}
