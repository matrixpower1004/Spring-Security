package com.matrix.bank.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
@AutoConfigureMockMvc   // Mock(가짜) 환경에 MockMvc 가 등록됨
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class SecurityConfigTest {

    // 가짜 환경에 등록된 MockMvc를 DI 한다.
    @Autowired
    private MockMvc mvc;

    // 서버는 일관성 있게 에러가 리턴되어야 한다.
    // 내가 모르는 에러가 프론트한테 날라가지 않게, 내가 직접 다 제어하자.
    @Test
    void authentication_test() throws Exception {
        // Given

        // When
        ResultActions resultActions = mvc.perform(get("/api/s/hello"));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        int httpStatusCode = resultActions.andReturn().getResponse().getStatus();

        System.out.println("테스트 : " + responseBody);
        System.out.println("테스트 : " + httpStatusCode);

        // Then
        assertThat(httpStatusCode).isEqualTo(401);
    }

    @Test
    void authorization_test() throws Exception {
        // Given

        // When
        ResultActions resultActions = mvc.perform(get("/api/admin/hello"));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        int httpStatusCode = resultActions.andReturn().getResponse().getStatus();

        System.out.println("테스트 : " + responseBody);
        System.out.println("테스트 : " + httpStatusCode);

        // Then
        assertThat(httpStatusCode).isEqualTo(401);
    }
}