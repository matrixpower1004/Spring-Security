package com.matrix.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrix.bank.config.auth.LoginUser;
import com.matrix.bank.config.dummy.DummyObject;
import com.matrix.bank.domain.user.User;
import com.matrix.bank.domain.user.UserEnum;
import com.matrix.bank.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static com.matrix.bank.dto.user.UserReqDto.JoinReqDto.LoginReqDto;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * author         : Jason Lee
 * date           : 2023-07-25
 * description    :
 */
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class JwtAuthorizationFilterTest extends DummyObject {

    @Autowired
    private MockMvc mvc;

    /**
     * Authorization test 는 doFilterInternal() 메서드를 테스트하는 것이다.
     * 클라이언트가 토큰을 들고 있는 상태에서 Authorization 을 받는 것을 테스트 한다.
     */
    @Test
    void authorization_success_test() throws Exception {
        // Given
        User user = User.builder()
                .id(1L)
                .role(UserEnum.CUSTOMER)
                .build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("테스트 : " +jwtToken);

        // When
        // 인증이 필요한 페이지 url 로 요청한다.
        ResultActions resultActions = mvc.perform(get("/api/s/hello/test")
                .header(JwtVO.HEADER, jwtToken));

        // Then
        // 저런 페이지는 없기 때문에 인증을 통과는 하지만 404 에러가 내려와야 한다.
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void authorization_fail_test() throws Exception {
        // Given

        // When
        // jwt 토큰 헤더 없이 요청했을 때 인증 관련 예외가 발생해야 한다.
        ResultActions resultActions = mvc.perform(get("/api/s/hello/test"));

        // Then
        resultActions.andExpect(status().isUnauthorized()); // 401 error
    }

    @Test
    void authorization_admin_role_success_test() throws Exception {
        // Given
        User user = User.builder()
                .id(1L)
                .role(UserEnum.ADMIN)
                .build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("테스트 : " +jwtToken);

        // When
        // 인증이 필요한 페이지 url 로 요청한다.
        ResultActions resultActions = mvc.perform(get("/api/admin/hello/test")
                .header(JwtVO.HEADER, jwtToken));

        // Then
        // 저런 페이지는 없기 때문에 인증을 통과는 하지만 404 에러가 내려와야 한다.
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void authorization_admin_role_fail_test() throws Exception {
        // Given
        User user = User.builder()
                .id(1L)
                .role(UserEnum.CUSTOMER)
                .build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("테스트 : " +jwtToken);

        // When
        ResultActions resultActions = mvc.perform(get("/api/admin/hello/test")
                .header(JwtVO.HEADER, jwtToken));

        // Then
        resultActions.andExpect(status().isForbidden()); // 403
    }
}