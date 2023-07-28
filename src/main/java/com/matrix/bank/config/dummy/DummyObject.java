package com.matrix.bank.config.dummy;

import com.matrix.bank.domain.account.Account;
import com.matrix.bank.domain.transaction.Transaction;
import com.matrix.bank.domain.transaction.TransactionEnum;
import com.matrix.bank.domain.user.User;
import com.matrix.bank.domain.user.UserEnum;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
public class DummyObject {

    // 계좌 1111L, 1000원
    // 입금 트랜잭션 -> 계좌 100원 변경 후 -> 입금 트랜잭션 히스토리가 생성되어야 함.
    protected static Transaction newMockDeposiTransaction(Long id, Account account) {
        account.deposit(100L);
        Transaction transaction = Transaction.builder()
                .id(id)
                .withdrawAccount(null)
                .depositAccount(account)
                .withdrawAccountBalance(null)
                .depositAccountBalance(account.getBalance())
                .amount(100L)
                .classify(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(String.valueOf(account.getNumber()))
                .tel("01012345678")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return transaction;

    }


    // entity에 save() 할 때 사용
    protected User newUser(String username, String fullname) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        // 진짜 객체의 id와 날짜는 DB에서 자동으로 생성되므로 필요가 없다.
        return User.builder()
                .username(username)
                .password(encPassword)
                .email(username + "@gmail.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .build();
    }

    // Mock stub 용도
    protected User newMockUser(Long id, String username, String fullname) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .id(id)
                .username(username)
                .password(encPassword)
                .email(username + "@gmail.com")
                .fullname(fullname)
                .role(UserEnum.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    protected Account newAccount(Long number, User user) {
        return Account.builder()
                .number(number)
                .password(1234L)
                .balance(1000L)
                .user(user)
                .build();
    }

    // 가짜로 DB 저장되어 있는것과 똑같이 하나를 만들어 낸다는 의미
    protected Account newMockAccount(Long id, Long number, Long balance, User user) {
        return Account.builder()
                .id(id)
                .number(number)
                .password(1234L)
                .balance(balance)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
