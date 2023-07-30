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
- `@BeforeEach` 로 테스트에 사용할 실제 데이터를 DB에 insert 한다면 클래스 레벨에 `@Transactional` 어노테이션을 붙여서 테스트 후 rollback 되도록 해준다.
- application.yml 의 `'[org.hibernate.type]': TRACE` : Dynamic Query 의 ? 에 들어가는 값까지 확인할 수 있다.
- SecurityConfig 작성시 주의사항
  - 최근 공식문서에 따르면 hasRole() 메서드에 더이상 접두사 "ROLE_" 를 붙이지 않는다. 붙이면 오히려 Exception 이 발생한다.
  - reference : https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
```java
http.authorizeRequests()
    .antMatchers("/api/s/**").authenticated()
    .antMatchers("/api/admin/**").hasRole(UserEnum.ADMIN.name())
    .anyRequest().permitAll();
return http.build();
```
- 기본적으로 SecurityContext는 TestExecutionListener.beforeTestMethod 이벤트 중에 설정된다. 이는 JUnit의 `@Before` 애너테이션으로 등록한 메서드가 실행되기 이전에 설정된다는 의미이다.
이를 변경하여 테스트 메서드가 호출되기 전, 즉 JUnit의 `@Before` 이후에 TestExecutionListener.beforeTestExecution 이벤트 중에 발생하도록 할 수 있다.
- reference : https://docs.spring.io/spring-security/reference/5.7/servlet/test/method.html#test-method-withuserdetails
```java
@WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
```
- `@Sql("classpath:sql/teardown.sql")` 을 적용하는 이유
  - 단순히 `@Transsaction` 애너테이션으로 롤백만 해서는 DB에서 auto_increament로 증가되는 id 값들의 초기화가 되지 않아서 테스트가 쌓이다 보면 우리가 원하지 않는 엉뚱한 값이 나올 수 있기 때문에 깔끔하게 초기화를 해준다.
  - 이때 table 자체를 drop 하게 되면 불필요한 create table 을 하기 때문에 truncate로 데이터만 날린다.
  - DB와 관련있는 Controller Test를 작성할 때는 아래 4개 애너테이션을 기본적으로 붙이고 시작하자.
  - `@Sql("classpath:db/teardown.sql")` 애너테이션을 붙여주면 `@BeforeEach` 실행 직전마다 teardown.sql 을 실행한다.
```java
@Sql("classpath:db/teardown.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
```
- teardown.sql
```sql
SET REFERENTIAL_INTEGRITY FALSE;
truncate table transaction_tb;
truncate table account_tb;
truncate table user_tb;
SET REFERENTIAL_INTEGRITY TRUE;
```
- Service Test에서 stub이 진행될 때 마다 연관된 객체는 새로 만들어서 주입한다. stub1에서 만든 객체를 stub2에서 연결해서 사용하면 타이밍 때문에 값이 꼬인다.
- `@Valid` 애네테이션을 붙인 객체 바로 뒤에 BindingResult가 없으면 Validation이 되지 않는다. -> `주의` 

## Repository Test 시 주의사항
- Repository 테스트 할 때 더미 데이터를 insert 했따면 꼭 insert 후 `em.clear()` 를 실행하여 Persistence Context를 초기화 해줘야 한다. 그렇지 않으면 영속성 컨텍스트에 캐시된 데이터로 인하여 정확한 테스트가 되지 않는다. Repository Test 에서 필수.
- Repository 테스트 시 아래 2개 애너테이션을 붙여주고 시작하자
```java
@ActiveProfiles("test")
@DataJpaTest
```
- Repository 테스트 시 auto_increment 초기화는 Service 테스트와는 방법이 다르다. 아래 코드와 같이 autoIncrement를 reset 하는 메서드를 만들고 `@BeforeEach`에서 호출하자.
```java
@Autowired
private EntityManager em;

@BeforeEach
public final void setUp() {
    autoIncrementReset();
    dataSetting();
    em.clear(); // Repository test에서 필수
    }

private void autoIncrementReset() {
	em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
	em.createNativeQuery("ALTER TABLE account_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
	em.createNativeQuery("ALTER TABLE transaction_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
}
```
- em.clear() 는 DB 관련 테스트에서 필수적으로 넣어야 Persistence Context의 캐시에 의한 오류를 방지할 수 있다.
## CORS-safelisted response header
- CORS 허용 목록에 포함된 응답 헤더는 클라이언트 스크립트에 노출해도 안전한 것으로 간주되는 CORS 응답의 HTTP 헤더다.
- 이 허용 목록에 포함된 응답 헤더만 클라이언트 스크립트(Javascript 등)에서 접근할 수 있다.
- 클라이언트 스크립트에서 접근 가능한 노출 헤더를 추가하고 싶다면 Security 설정에서 Access-Control-Expose-Headers에 넣어주면 된다.
- reference : https://developer.mozilla.org/en-US/docs/Glossary/CORS-safelisted_response_header
- Spring Security 에서는 아래와 같이 설정을 추가해 준다.
```java
public CorsConfigurationSource configurationSource(){
    ...
    configuration.addExposedHeader("Authorization");
    ...
    return source;
}
```
