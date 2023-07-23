package com.matrix.bank.domain.account;

import com.matrix.bank.domain.user.User;
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
@Table(name = "account_tb")
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
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
}
