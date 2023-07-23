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

### 주요 학습 노트
- 스프링이 객체를 생성할 때 빈 생성자로 new 을 하기 때문에 parameter를 주입 받는 생성자를 수동으로 만들었다면 `@NoArgsConstructor` 를 꼭 넣어줘야 한다.
- Entity 객체 생성 시 아래 애너테이션들은 거의 필수.
```java
@NoArgsConstructor  // 스프링이 User 객체를 생성할 때 빈생성자로 new를 하기 때문
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_tb")
@Entity
```
- ServiceTest에서 Mock 객체와 진짜 객체 주입 방법
```java
    @InjectMocks    // @Mock과 @Spy에 선언된 객체를 주입받는다.
    private UserService userService;

    // 이건 실제로 띄울 필요가 없기 때문에 Mock으로 만들어준다.
    @Mock   // 가짜 객체를 주입해준다.
    private UserRepository userRepository;

    @Spy    // 실제 객체를 주입해준다.
    private BCryptPasswordEncoder passwordEncoder;
```
