package com.matrix.bank.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrix.bank.config.dummy.DummyObject;
import com.matrix.bank.domain.account.Account;
import com.matrix.bank.domain.account.AccountRepository;
import com.matrix.bank.domain.user.User;
import com.matrix.bank.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.matrix.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import static com.matrix.bank.dto.account.AccountRespDto.AccountListRespDto;
import static com.matrix.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * author         : Jason Lee
 * date           : 2023-07-26
 * description    :
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest extends DummyObject {

    @InjectMocks // 모든 Mock들이 InjectMocks에 주입된다.
    private AccountService accountService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Spy // 진짜 객체를 InjectMocks에 주입한다.
    private ObjectMapper om;

    @Test
    void create_account_test() throws JsonProcessingException {
        // Given
        Long mockUserId = 1L;

        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto(1111L, 1234L);

        // stub1
        User mockUser = newMockUser(mockUserId, "bank", "돈이좋아");
        when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));

        // stub2
        when(accountRepository.findByNumber(any())).thenReturn(Optional.empty());

        // stub3
        Account mockAccount = newMockAccount(1L, 1111L, 1000L, mockUser);
        when(accountRepository.save(any())).thenReturn(mockAccount);

        // When
        AccountSaveRespDto accountSaveRespDto = accountService.createAccount(accountSaveReqDto, mockUserId);
        String responseBody = om.writeValueAsString(accountSaveRespDto);
        System.out.println("테스트 : " + responseBody);

        // Then
        assertThat(accountSaveRespDto.getNumber()).isEqualTo(1111L);
    }

    @Test
    void view_account_list_by_user_test() throws Exception {
        // Given
        Long userId= 1L;

        // stub
        User mockUser = newMockUser(userId, "bank", "돈이좋아");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        Account mockAccount1 = newMockAccount(1L, 1111L, 1000L, mockUser);
        Account mockAccount2 = newMockAccount(2L, 2222L, 1000L, mockUser);
        List<Account> accountList = Arrays.asList(mockAccount1, mockAccount2);
        when(accountRepository.findByUser_id(userId)).thenReturn(accountList);

        // When
        AccountListRespDto accountListRespDto = accountService.viewAccountListByUser(userId);

        // Then
        assertThat(accountListRespDto.getFullname()).isEqualTo("돈이좋아");
        assertThat(accountListRespDto.getAccounts().size()).isEqualTo(2);
    }

}
