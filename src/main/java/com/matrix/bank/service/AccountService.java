package com.matrix.bank.service;

import com.matrix.bank.domain.account.Account;
import com.matrix.bank.domain.account.AccountRepository;
import com.matrix.bank.domain.transaction.Transaction;
import com.matrix.bank.domain.transaction.TransactionEnum;
import com.matrix.bank.domain.transaction.TransactionRepository;
import com.matrix.bank.domain.user.User;
import com.matrix.bank.domain.user.UserRepository;
import com.matrix.bank.handler.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.matrix.bank.dto.account.AccountReqDto.*;
import static com.matrix.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import static com.matrix.bank.dto.account.AccountRespDto.*;
import static com.matrix.bank.dto.account.AccountRespDto.AccountListRespDto;
import static com.matrix.bank.dto.account.AccountRespDto.AccountSaveRespDto;

/**
 * author         : Jason Lee
 * date           : 2023-07-25
 * description    :
 */
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public AccountSaveRespDto createAccount(AccountSaveReqDto accountSaveReqDto, Long userId) {
        // User 로그인 되어 있는 상태. User 로그인 되어 있는지 상태 체크는 Controller 의 역할.
        // 1. User DB에 있는지 먼저 검증 겸 User entity 가져오기
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다. id = " + userId));

        // 2. 햬당 계좌가 DB에 있는지 중복 여부 체크
        Optional<Account> accountOP = accountRepository.findByNumber(accountSaveReqDto.getNumber());
        if (accountOP.isPresent()) {
            throw new CustomApiException("이미 존재하는 계좌입니다.");
        }

        // 3. 계좌 생성
        Account accountPS = accountRepository.save(accountSaveReqDto.toEntity(userPS));

        // 4. DTO를 응답
        return new AccountSaveRespDto(accountPS);
    }

    public AccountListRespDto viewAccountListByUser(Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다. id = " + userId));

        // 유저의 모든 계좌 목록
        List<Account> accountListPS = accountRepository.findByUser_id(userId);
        // 이걸 return 하려면 Dto가 필요하다.
        return new AccountListRespDto(userPS, accountListPS);
    }

    @Transactional
    public void deleteAccount(Long number, Long userId) {
        // 1. 계좌가 실제로 존재하는지 확인
        Account accountPS = accountRepository.findByNumber(number).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다. 계좌번호 = " + number)
        );

        // 2. 계좌 소유자 확인 : 로그인한 유저의 id와 계좌 소유자의 id가 같은지 확인
        accountPS.checkOwner(userId);

        // 3. 계좌 삭제
        accountRepository.deleteById(accountPS.getId());
    }

    // 계좌 입금 -> 인증이 필요 없다.
    @Transactional
    public AccountDepositRespDto depositAccount(AccountDepositReqDto accountDepositReqDto) { // ATM -> 누군가의 계좌

        // 0원 체크. 입급하는데 1원 이하면 처리할 필요가 있을까?
        checkAmount.accept(accountDepositReqDto.getAmount());

        // 입금 계좌가 존재하는지 확인
        Account depositAccountPS = accountRepository.findByNumber(accountDepositReqDto.getNumber())
                .orElseThrow(
                        () -> new CustomApiException("입금 계좌를 찾을 수 없습니다. 계좌번호 = "
                                + accountDepositReqDto.getNumber())
                );

        // 임금 (해당 계좌 balance 조정 - update 쿼리 - 더티 체킹)
        depositAccountPS.deposit(accountDepositReqDto.getAmount());
        // Todo. 배포 전에 삭제할 것
//        System.out.println("테스트 - account1 잔액 : " + depositAccountPS.getBalance());

        // 거래 내역 남기기
        Transaction transaction = Transaction.builder()
                .depositAccount(depositAccountPS)
                .withdrawAccount(null)
                .depositAccountBalance(depositAccountPS.getBalance())
                .withdrawAccountBalance(null)
                .amount(accountDepositReqDto.getAmount())
                .classify(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(String.valueOf(depositAccountPS.getNumber()))
                .tel(accountDepositReqDto.getTel())
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);

        // 계좌 입급이 잘되었다고 응답을 하는 Dto를 만들어준다.
        // Entity를 Controller 쪽으로 응답을 하지 않는다. Lazy loading 때문에 문제가 발생할 수 있다.
        return new AccountDepositRespDto(depositAccountPS, transactionPS);
    }

    @Transactional
    public AccountWithdrawRespDto withdrawAccount(AccountWithdrawReqDto accountWithdrawReqDto, Long userId) {
        // 1. 계좌 출금시 필요한 Dto를 만들어야 한다.
        // 2. 출금할 때는 로그인이 되어 있어야 하므로 userId를 받아야 한다.
        // 3. 0원 체크를 해야 한다.
        // 4. 출금 계좌의 존재여부를 확인 해야 한다.

        // 0원 체크
        checkAmount.accept(accountWithdrawReqDto.getAmount());

        // 출금 계좌가 존재하는지 확인
        Account withdrawAccountPS = accountRepository.findByNumber(accountWithdrawReqDto.getNumber())
                .orElseThrow(
                        () -> new CustomApiException("츨금 계좌를 찾을 수 없습니다. 계좌번호 = "
                                + accountWithdrawReqDto.getNumber())
                );

        // 출금 계좌 소유자와 로그인한 유저가 같은지 확인
        withdrawAccountPS.checkOwner(userId);

        // 출금 계좌 비밀번호 확인
        withdrawAccountPS.checkSamePassword(accountWithdrawReqDto.getPassword());

        // 출금 계좌 잔액 확인
        withdrawAccountPS.checkBalance(accountWithdrawReqDto.getAmount());

        // 출금하기
        withdrawAccountPS.withdraw(accountWithdrawReqDto.getAmount());

        // 거래내역 남기기 (네 계좌 -> ATM 츨금)
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS)
                .depositAccount(null)
                .withdrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccountBalance(null)
                .amount(accountWithdrawReqDto.getAmount())
                .classify(TransactionEnum.WITHDRAW)
                .sender(String.valueOf(accountWithdrawReqDto.getNumber()))
                .receiver("ATM")
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);

        // DTO 응답
        return new AccountWithdrawRespDto(withdrawAccountPS, transactionPS);
    }

    @Transactional
    public AccountTransferRespDto transferAccount(AccountTransferReqDto accountTransferReqDto, Long userId) {

        // 출금계좌와 입급계좌가 동일하면 안 된다.
        checkSameAccount.accept(accountTransferReqDto.getWithdrawNumber(),
                accountTransferReqDto.getDepositNumber());

        // 0원 체크
        checkAmount.accept(accountTransferReqDto.getAmount());

        // 출금 계좌가 존재하는지 확인
        Account withdrawAccountPS = accountRepository.findByNumber(accountTransferReqDto.getWithdrawNumber())
                .orElseThrow(
                        () -> new CustomApiException("츨금 계좌를 찾을 수 없습니다. 계좌번호 = "
                                + accountTransferReqDto.getWithdrawNumber())
                );

        // 입금 계좌가 존재하는지 확인
        Account depositAccountPS = accountRepository.findByNumber(accountTransferReqDto.getDepositNumber())
                .orElseThrow(
                        () -> new CustomApiException("입금 계좌를 찾을 수 없습니다. 계좌번호 = "
                                + accountTransferReqDto.getWithdrawNumber())
                );

        // 출금 계좌 소유자와 로그인한 유저가 같은지 확인
        withdrawAccountPS.checkOwner(userId);

        // 출금 계좌 비밀번호 확인
        withdrawAccountPS.checkSamePassword(accountTransferReqDto.getWithdrawPassword());

        // 출금 계좌 잔액 확인
        withdrawAccountPS.checkBalance(accountTransferReqDto.getAmount());

        // 이체하기
        withdrawAccountPS.withdraw(accountTransferReqDto.getAmount());
        depositAccountPS.deposit(accountTransferReqDto.getAmount());

        // 거래내역 남기기 (내 계좌 -> ATM 츨금)
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS)
                .depositAccount(depositAccountPS)
                .withdrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccountBalance(depositAccountPS.getBalance())
                .amount(accountTransferReqDto.getAmount())
                .classify(TransactionEnum.TRANSFER)
                .sender(String.valueOf(accountTransferReqDto.getWithdrawNumber()))
                .receiver(String.valueOf(accountTransferReqDto.getDepositNumber()))
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);

        // DTO 응답
        return new AccountTransferRespDto(withdrawAccountPS, transactionPS);
    }

    public AccountDetailRespDto viewAccountDetail(Long number, Long userId, int page) {
        // 1. 구분값은 고정 -> ALL
        String classify = "ALL";

        // 2. 계좌번호가 존재하는지 확인
        Account accountPS = accountRepository.findByNumber(number).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다. 계좌번호 = " + number)
        );

        // 3. 계좌 소유자와 로그인한 유저가 같은지 확인
        accountPS.checkOwner(userId);

        // 4. 입출금 목록보기
        List<Transaction> transactionListPS = transactionRepository.findTransactionList(
                accountPS.getId(), classify, page);

        return new AccountDetailRespDto(accountPS, transactionListPS);
    }

    public static final Consumer<Long> checkAmount = amount -> {
        if (amount < 1L) {
            throw new CustomApiException("입금 금액은 1원 이상이어야 합니다.");
        }
    };

    public static final BiConsumer<Long, Long> checkSameAccount = (
            withdrawNumber, depositNumber) -> {
        if (withdrawNumber.longValue() == depositNumber.longValue()) {
            throw new CustomApiException("출금계좌와 입금계좌가 같을 수 없습니다.");
        }
    };
}
