# Junit Bank App

### Jpa LocalDateTime 자동으로 생성하는 법
- @EnableJpaAuditing (Main Application 에)
- @EntityListeners(AuditingEntityListener.class) (Entity class 에)
```java
    @CreatedDate    // insert 할 때 자동으로 날짜가 들어감
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate   // insert, update 할 때 자동으로 날짜가 들어감
    @Column(nullable = false)
    private LocalDateTime updatedAt;
```

### Project Moeno
- 스프링이 객체를 생성할 때 빈 생성자로 new 을 하기 때문에 `@AllArgsConstructor` 를 사용했다면 `@NoArgsConstructor` 를 꼭 넣어줘야 한다.
- Entity 객체 생성 시 아래 애너테이션들은 필수
```java
@NoArgsConstructor  // 스프링이 User 객체를 생성할 때 빈생성자로 new를 하기 때문
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_tb")
@Entity
```
 
"# Spring-Security" 
