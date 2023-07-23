package com.matrix.bank.domain.user;

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
@Table(name = "user_tb")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 60)  // password encoding(BCrypt)
    private String password;

    @Column(nullable = false, length = 20)
    private String email;
    @Column(nullable = false, length = 20)
    private String fullname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserEnum role;  // ADMIN, CUSTOMER

    @CreatedDate    // insert 할 때 자동으로 날짜가 들어감
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate   // insert, update 할 때 자동으로 날짜가 들어감
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public User(Long id, String username, String password, String email, String fullname, UserEnum role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
