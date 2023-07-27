package com.matrix.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrix.bank.config.dummy.DummyObject;
import com.matrix.bank.domain.user.UserRepository;
import com.matrix.bank.dto.user.UserReqDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * author         : Jason Lee
 * date           : 2023-07-25
 * description    :
 */
// SpringBootTest 하는 곳에는 전부 다 teardown.sql 을 붙여주자.
@Sql("classpath:db/teardown.sql") // 실행시점 : @BeforeEach 실행 직전마다!!
// '[org.hibernate.type]': TRACE -> 쿼리에 들어가는 값까지 확인할 수 있다.
@ActiveProfiles("test") // application-test.yml 설정을 사용한다.
@AutoConfigureMockMvc // Mockito 환경에서 MockMvc를 사용할 수 있게 해준다.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class JwtAuthenticationFilterTest extends DummyObject {

    @Autowired
    private ObjectMapper om;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.save(newUser("bank", "쌀"));
    }

    /**
     * attemptAuthentication() 메서드 실행 테스트.
     * 로그인 실패 시 unsuccessfulAuthentication() 메서드 실행 여부
     * 로그인 성공 시 successfulAuthentication() 메서드 실행 여부
     */
    @Test
    void successfulAuthentication_test() throws Exception {
        // Given
        UserReqDto.JoinReqDto.LoginReqDto loginReqDto = new UserReqDto.JoinReqDto.LoginReqDto();
        loginReqDto.setUsername("bank");
        loginReqDto.setPassword("1234");

        // request에 json 형태로 데이터가 날아오기 때문에 변환을 위한 ObjectMapper 가 필요하다.
        String requestBody = om.writeValueAsString(loginReqDto);
        System.out.println("테스트 : " + requestBody);

        // When
        // 테스트하려는 attemptAuthentication() 메서드는 POST 요청시에만 작동한다.
        ResultActions resultActions = mvc.perform(post("/api/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);
        System.out.println("테스트 : " + responseBody);
        System.out.println("테스트 : " + jwtToken);

        // Then
        resultActions.andExpect(status().isOk());
        assertNotNull(jwtToken);
        assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
        resultActions.andExpect(jsonPath("$.data.username").value("bank"));
    }

    @Test
    void unsuccessfulAuthentication_test() throws Exception {
        // Given
        UserReqDto.JoinReqDto.LoginReqDto loginReqDto = new UserReqDto.JoinReqDto.LoginReqDto();
        loginReqDto.setUsername("bank");
        loginReqDto.setPassword("12345");

        // request에 json 형태로 데이터가 날아오기 때문에 변환을 위한 ObjectMapper 가 필요하다.
        String requestBody = om.writeValueAsString(loginReqDto);
        System.out.println("테스트 : " + requestBody);

        // When
        ResultActions resultActions = mvc.perform(post("/api/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);
        System.out.println("테스트 : " + responseBody);
        System.out.println("테스트 : " + jwtToken);

        // Then
        resultActions.andExpect(status().isUnauthorized());
    }
}
