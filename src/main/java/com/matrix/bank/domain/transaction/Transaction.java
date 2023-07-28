package com.matrix.bank.domain.transaction;

import com.matrix.bank.domain.account.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * author         : Jason Lee
 * date           : 2023-07-22
 * description    :
 */
@NoArgsConstructor  // 스프링이 User 객체를 생성할 때 빈생성자로 new를 하기 때문
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "transaction_tb")
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account withdrawAccount;    // 출금계좌

    @ManyToOne(fetch = FetchType.LAZY)
    private Account depositAccount;     // 입금계좌

    private Long amount;    // 거래금액
    private Long withdrawAccountBalance;   // 출금계좌 잔액의 history
    private Long depositAccountBalance;    // 입금계좌 잔액의 history

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionEnum classify;  // WHTHDRAW, DEPOSIT, TRANSFER, ALL

    // 계좌가 사라져도 로그는 남아야 한다.
    private String sender;
    private String receiver;
    private String tel;

    @CreatedDate    // insert
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate   // insert, update
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Transaction(Long id, Account withdrawAccount, Account depositAccount, Long amount, Long withdrawAccountBalance, Long depositAccountBalance, TransactionEnum classify, String sender, String receiver, String tel, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.withdrawAccount = withdrawAccount;
        this.depositAccount = depositAccount;
        this.amount = amount;
        this.withdrawAccountBalance = withdrawAccountBalance;
        this.depositAccountBalance = depositAccountBalance;
        this.classify = classify;
        this.sender = sender;
        this.receiver = receiver;
        this.tel = tel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
