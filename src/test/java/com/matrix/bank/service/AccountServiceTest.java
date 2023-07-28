package com.matrix.bank.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrix.bank.config.dummy.DummyObject;
import com.matrix.bank.domain.account.Account;
import com.matrix.bank.domain.account.AccountRepository;
import com.matrix.bank.domain.transaction.Transaction;
import com.matrix.bank.domain.transaction.TransactionRepository;
import com.matrix.bank.domain.user.User;
import com.matrix.bank.domain.user.UserRepository;
import com.matrix.bank.dto.account.AccountRespDto;
import com.matrix.bank.handler.ex.CustomApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.matrix.bank.dto.account.AccountReqDto.AccountDepositReqDto;
import static com.matrix.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import static com.matrix.bank.dto.account.AccountRespDto.*;
import static com.matrix.bank.dto.account.AccountRespDto.AccountListRespDto;
import static com.matrix.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import static com.matrix.bank.service.AccountService.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private TransactionRepository transactionRepository;

    @Spy // 진짜 객체를 InjectMocks에 주입한다.
    private ObjectMapper om;

    @DisplayName("계좌 생성 테스트")
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

    @DisplayName("계좌목록보기 유저별 테스트")
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

    @DisplayName("계좌 삭제 테스트")
    @Test
    void account_delete_test() {
        // Given
        Long number = 1111L;
        Long userId = 2L;

        // stub : findByNumber에 대한 stub이 필요하다.
        User mockUser = newMockUser(1L, "bank", "돈이좋아");
        Account mockAccount = newMockAccount(1L, number, 10000L, mockUser);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(mockAccount));

        // When & Then
        assertThrows(CustomApiException.class, () -> accountService.deleteAccount(number, userId));
    }

    // 입금이 되었을 때 확인해야 할 사항
    // Account -> balance 변경됐는지
    // Transaction -> balance 잘 기록됐는지
    @DisplayName("계좌 입금 테스트 1")
    @Test
    void account_deposit_test() throws Exception {
        // Given
        // 우선 Dto부터 만들어야 한다.
        AccountDepositReqDto accountDepositReqDto =
                AccountDepositReqDto.builder()
                        .number(1111L)
                        .amount(100L)
                        .classify("DEPOSIT")
                        .tel("010123345678")
                        .build();

        // stub1 : accountRepository.findByNumber(accountDepositReqDto.getNumber())
        // stub1에서는 계좌를 찾아내고
        User mockUser = newMockUser(1L, "bank", "돈이좋아"); // 실행됨
        Account mockAccount1 = newMockAccount(1L, 1111L, 1000L, mockUser); // 실행됨 - mockAccount1 -> 1000원

        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(mockAccount1)); // 실행 안 됨. -> service 호출 후 실행됨 -> 1100원

        // stub1 과 stub2의 값들을 서로 연결해서 쓰면 안 된다. 테스트 결과가 꼬일 수 있다. MockUser는 테스트 할 때마다 새로 만드는 게 좋다.
        // stub2 : transactionRepository.save(transaction)
        // 스텁이 진행될 때 마다 연관된 객체는 새로 만들어서 주입하기 - 타이밍 때문에 꼬인다.
        Account mockAccount2 = newMockAccount(1L, 1111L, 1000L, mockUser); // 실행됨 - mockAccount2 -> 1000원
        Transaction mockTransaction = newMockDeposiTransaction(1L, mockAccount2); // 실행됨 - (mockAccount1 -> 1100원) (transaction -> 1100원)

        when(transactionRepository.save(any())).thenReturn(mockTransaction); // 실행 안 됨.

        // When
        AccountDepositRespDto accountDepositRespDto = accountService.depositAccount(accountDepositReqDto);
        System.out.println("테스트 - 트랜잭션 입금계좌 잔액 : " + accountDepositRespDto.getTransaction().getDepositAccountBalance());
        System.out.println("테스트 - 계좌쪽 잔액 : " + mockAccount1.getBalance());
        System.out.println("테스트 - 계좌쪽 잔액 : " + mockAccount2.getBalance());

        // Then
        assertThat(mockAccount1.getBalance()).isEqualTo(1100L);
        assertThat(accountDepositRespDto.getTransaction().getDepositAccountBalance()).isEqualTo(1100L);
    }

    @DisplayName("계좌 입금 테스트 2")
    @Test
    void account_deposit_test2() throws Exception {
        // Given
        AccountDepositReqDto accountDepositReqDto =
                AccountDepositReqDto.builder()
                        .number(1111L)
                        .amount(100L)
                        .classify("DEPOSIT")
                        .tel("010123345678")
                        .build();

        // stub 1
        User mockUser1 = newMockUser(1L, "bank", "돈이좋아");
        Account mockAccount1 = newMockAccount(1L, 1111L, 1000L, mockUser1);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(mockAccount1));

        // stub 2 - 계좌 입금 되기 전에 두번째 stub이 필요하다
        // 넣을 때는 stub마다 독립적인 데이터를 넣어야 한다.
        User mockUser2 = newMockUser(1L, "bank", "돈이좋아");
        Account mockAccount2 = newMockAccount(1L, 1111L, 1000L, mockUser2);
        Transaction mockTransaction = newMockDeposiTransaction(1L, mockAccount2);
        when(transactionRepository.save(any())).thenReturn(mockTransaction);

        // When
        AccountDepositRespDto accountDepositRespDto = accountService.depositAccount(accountDepositReqDto);
        String responseBody = om.writeValueAsString(accountDepositRespDto);
        System.out.println("테스트 : " + responseBody);

        // Then
        // 우리의 관심사는 transaction에 dto가 잘 만들어졌는지가 궁금한 것.
        assertThat(mockAccount1.getBalance()).isEqualTo(1100L);
    }


    // 서비스 테스트를 보여드린 것은, 기술적인 테크닉!!
    // 진짜 서비스를 테스트하고 싶으면, 내가 지금 무엇을 여기서 테스트해야할지 명확히 구분 (책임 분리)
    // DTO를 만드는 책임 -> 서비스에 있지만!! (서비스에서 DTO 검증 안 할래!! - Controller에서 테스트 하니까)
    // DB 관련된 것도 -> 서비스 책임이 아니야. 볼필요 없어.
    // DB 관련된 것을 조회했을 때, 그 값을 통해서 비즈니스 로직이 흘러가는 것이 필요하면 -> stub으로 정의해서 테스트 해보면 된다.

    // DB 스텁, DB 스텁(가짜로 DB 만들어서 deposit 검증, 0원 검증) -> 굳이 이렇게 할 필요는 없다.
    @Test
    void account_deposit_test3() {
        // Given
        Account account = newMockAccount(1L, 1111L, 1000L, null);
        Long amount = 100L;

        // When
        isInvalidAmount.accept(amount); // 1원 이하 입금 체크
        account.deposit(100L);

        // Then
        assertThat(account.getBalance()).isEqualTo(1100L);
    }
}
