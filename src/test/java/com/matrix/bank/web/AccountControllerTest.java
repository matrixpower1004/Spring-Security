package com.matrix.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrix.bank.config.dummy.DummyObject;
import com.matrix.bank.domain.user.User;
import com.matrix.bank.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.matrix.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * author         : Jason Lee
 * date           : 2023-07-26
 * description    :
 */
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AccountControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user = userRepository.save(newUser("bank", "돈이좋아"));
    }

    // jwt token -> 인증필터 -> 시큐리티 세션생성
    // setupBefore=TEST_METHOD (setUp 메소드 실행 전에 실행)
    // setupBefore = TEST_EXECUTION (save_account_test() 메서드 실행전에 수행)
    @WithUserDetails(value = "bank", setupBefore = TestExecutionEvent.TEST_EXECUTION) // DB에서 username = ssar 조회를 해서 세션에 담아주는 어노테이션
    @Test
    void save_account_test() throws Exception {
        // Given
        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto(9999L, 1234L);
        String responseBody = om.writeValueAsString(accountSaveReqDto);
        System.out.println("테스트 : " + responseBody);

        // When
        // 시큐리티에 세션이 존재한다면 "/api/s/account"로 접근 가능
        ResultActions resultActions = mvc.perform(post("/api/s/account")
                .content(responseBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody2 = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody2);

        // Then
        resultActions.andExpect(status().isCreated());
    }
}