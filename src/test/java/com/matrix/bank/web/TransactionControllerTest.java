package com.matrix.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrix.bank.config.dummy.DummyObject;
import com.matrix.bank.domain.account.Account;
import com.matrix.bank.domain.account.AccountRepository;
import com.matrix.bank.domain.transaction.Transaction;
import com.matrix.bank.domain.transaction.TransactionRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * author         : Jason Lee
 * date           : 2023-07-30
 * description    :
 */
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class TransactionControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        dataSetting();
        em.clear();
    }

    @WithUserDetails(value = "bank", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void findTransactionList_test() throws Exception {
        // given
        Long number = 1111L;
        String classify = "ALL";
        String page = "0";

        // when
        ResultActions resultActions = mvc.perform(get("/api/s/account/" + number + "/transaction")
                .param("classify", classify)
                .param("page", page));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.transactions[0].balance").value(900L));
        resultActions.andExpect(jsonPath("$.data.transactions[1].balance").value(800L));
        resultActions.andExpect(jsonPath("$.data.transactions[2].balance").value(700L));
        resultActions.andExpect(jsonPath("$.data.transactions[3].balance").value(800L));
    }

    private void dataSetting() {
        User bank = userRepository.save(newUser("bank", "돈이좋아"));
        User matrix = userRepository.save(newUser("matrix", "매트릭스"));
        User mind = userRepository.save(newUser("mind", "마인드"));
        User admin = userRepository.save(newUser("admin", "관리자"));

        Account bankAccount1 = accountRepository.save(newAccount(1111L, bank));
        Account matrixAccount = accountRepository.save(newAccount(2222L, matrix));
        Account mindAccount = accountRepository.save(newAccount(3333L, mind));
        Account bankAccount2 = accountRepository.save(newAccount(4444L, bank));

        Transaction withdrawTransaction1 = transactionRepository
                .save(newWithdrawTransaction(bankAccount1, accountRepository));
        Transaction depositTransaction1 = transactionRepository
                .save(newDepositTransaction(matrixAccount, accountRepository));
        Transaction transferTransaction1 = transactionRepository
                .save(newTransferTransaction(bankAccount1, matrixAccount, accountRepository));
        Transaction transferTransaction2 = transactionRepository
                .save(newTransferTransaction(bankAccount1, mindAccount, accountRepository));
        Transaction transferTransaction3 = transactionRepository
                .save(newTransferTransaction(matrixAccount, bankAccount1, accountRepository));
    }

}
