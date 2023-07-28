package com.matrix.bank.domain.account;

import com.matrix.bank.domain.user.User;
import com.matrix.bank.handler.ex.CustomApiException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * author         : Jason Lee
 * date           : 2023-07-22
 * description    :
 */
@NoArgsConstructor  // 스프링이 User 객체를 생성할 때 빈 생성자로 new를 하기 때문
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "account_tb")
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 4) // 테스트의 편의를 위해 4자리로 수정
    private Long number;    // 계좌번호

    @Column(nullable = false, length = 4)
    private Long password;  // 비밀번호

    @Column(nullable = false)
    private Long balance;   // 잔액 (기본값 1,000원)

    // 한 명의 유저는 여러 계좌를 가질 수 있다.
    // 항상 ORM에서 FK의 주인은 Many Entity 쪽이다.
    @ManyToOne(fetch = FetchType.LAZY)  // account.getUser().아무필드호출() => 이때 Lazy가 발동
    private User user;  // default : user_id

    @CreatedDate    // insert
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate   // insert, update
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Account(Long id, Long number, Long password, Long balance, User user, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.number = number;
        this.password = password;
        this.balance = balance;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void checkOwner(Long userId) {
//        String testUserName = user.getUsername(); // Lazy 로딩이 되어야 함.
//        System.out.println("테스트 : " + testUserName);
//        System.out.printf("테스트 : user.getId = %d%n", user.getId());
//        System.out.printf("테스트 : userId = %d%n", userId);

        if (!user.getId().equals(userId)) { // Lazy 로딩이어도 id는 이미 메모리에 있기 때문에, id를 조회할 때는 select 쿼리가 날라가지 않는다.
            throw new CustomApiException("계좌의 소유주가 아닙니다.");
        }
    }

    public void deposit(Long amount) {
        this.balance += amount;
    }

    public void checkSamePassword(Long password) {
        // Long 타입은 ==(비교연산자) 로 비교하면 안된다.
        if (this.password.longValue() != password.longValue()) {
            throw new CustomApiException("계좌 비밀번호가 일치하지 않습니다.");
        }
    }

    public void checkBalance(Long amount) {
        if (this.balance < amount) {
            throw new CustomApiException("계좌의 잔액이 부족합니다.");
        }
    }

    public void withdraw(Long amount) {
        checkBalance(amount); // 안전하게 실행하기 위해서 잔액을 한번 더 체크
        this.balance -= amount;
    }
}
