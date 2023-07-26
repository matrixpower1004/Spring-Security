# Spring Security with Junit Bank App

### Jpa LocalDateTime 자동으로 생성하는 법
- `@EnableJpaAuditing` (Main Application 에)
- `@EntityListeners(AuditingEntityListener.class)` (Entity class 에)
```java
    @CreatedDate // insert 할 때 자동으로 날짜가 들어감
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // insert, update 할 때 자동으로 날짜가 들어감
    @Column(nullable = false)
    private LocalDateTime updatedAt;
```

### 학습 노트
- 스프링이 객체를 생성할 때 빈 생성자로 new 을 하기 때문에 parameter를 주입 받는 생성자를 수동으로 만들었다면 `@NoArgsConstructor` 를 꼭 넣어줘야 한다.
- Entity 객체 생성 시 아래 애너테이션들은 거의 필수.
```java
@NoArgsConstructor // 스프링이 User 객체를 생성할 때 빈 생성자로 new를 하기 때문에 꼭 넣어줘야 한다.
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_tb")
@Entity
```
- ServiceTest 시 참고할 어노테이션
```java
@Mock // 진짜 객체를 추상화된 가짜 객체로 만들어서 Mockito 환경에 주입한다.    
@InjectMocks // Mock 된 가까 객체를 진짜 객체인 Service를 만들어서 주입한다.
@MockBean // Mock 객체들을 스프링 ApplicationContext 에 주입한다. (IoC 컨테이너 주입)
@Spy // 실제 객체를 만들어서 Mockito 환경에 주입한다.
@SpyBean // Spy 객체들을 스프링 스프링 ApplicationContext 에 주입한다. (IoC 컨테이너 주입)
```
- SpringBootTest 주요 애너테이션 
```java
@ActiveProfiles("test") // application-test.yml 설정을 사용한다.
@AutoConfigureMockMvc // Mockito 환경에서 MockMvc를 사용할 수 있게 해준다.
@SprintBootTest(webEnvironment = WebEnvironment.MOCK) // 실제로 서버를 띄우지 않고 테스트를 진행할 수 있다.
```
- test 에서는 `@Transactional` 애너테이션의 기본 값이 rollback 으로 동작한다. 그래서 테스트가 끝나면 DB에 반영이 안 된다.
  - `@BeforeEach` 로 테스트에 사용할 실제 데이터를 DB에 insert 한다면 클래스 레벨에 `@Transactional` 어노테이션을 붙여줘야 한다.
- application.yml 의 `'[org.hibernate.type]': TRACE` : Dynamic Query 의 ? 에 들어가는 값까지 확인할 수 있다.
- SecurityConfig 작성시 주의사항
  - 최근 공식문서에 따르면 hasRole() 메서드에 더이상 접두사 "ROLE_" 를 붙이지 않는다. 붙이면 오히려 Exception 이 발생한다.
  - https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
```java
http.authorizeRequests()
    .antMatchers("/api/s/**").authenticated()
    .antMatchers("/api/admin/**").hasRole(String.valueOf(UserEnum.ADMIN))
    .anyRequest().permitAll();
return http.build();
```
- 기본적으로 SecurityContext는 TestExecutionListener.beforeTestMethod 이벤트 중에 설정된다. 이는 JUnit의 `@Before` 애너테이션으로 등록한 메서드가 실행되기 이전에 설정된다는 의미이다.
이를 변경하여 테스트 메서드가 호출되기 전, 즉 JUnit의 `@Before` 이후에 TestExecutionListener.beforeTestExecution 이벤트 중에 발생하도록 할 수 있다.
- reference : https://docs.spring.io/spring-security/reference/5.7/servlet/test/method.html#test-method-withuserdetails
```java
@WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
```
