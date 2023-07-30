package com.matrix.bank.domain.transaction;

import com.matrix.bank.config.dummy.DummyObject;
import com.matrix.bank.domain.account.Account;
import com.matrix.bank.domain.account.AccountRepository;
import com.matrix.bank.domain.user.User;
import com.matrix.bank.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * author         : Jason Lee
 * date           : 2023-07-29
 * description    :
 */
@ActiveProfiles("test")
@DataJpaTest // DB 관련된 Bean들만 IoC에 등록해준다.
class TransactionRepositoryImplTest extends DummyObject {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public final void setUp() {
        autoIncrementReset();
        dataSetting();
        em.clear(); // Repository test에서 필수
    }

    @DisplayName("입출금 내역 조회 테스트")
    @Test
    void findTransactionList_all_test() throws Exception {
        // given
        Long accountId = 1L;

        // when
        List<Transaction> transactionListPS =
                transactionRepository.findTransactionList(
                        accountId, "ALL", 0
                );
        transactionListPS.forEach((t) -> {
            System.out.println("테스트 - id: " + t.getId());
            System.out.println("테스트 - 금액 : " + t.getAmount());
            System.out.println("테스트 - 츨금 계좌 : " + t.getSender());
            System.out.println("테스트 - 입금 계좌 : " + t.getReceiver());
            System.out.println("테스트 - 출금계좌 잔액: " + t.getWithdrawAccountBalance());
            System.out.println("테스트 - 입금계좌 잔액 : " + t.getDepositAccountBalance());
            if (t.getWithdrawAccount() != null) { // ATM 입금은 출금계좌가 없으므로 NullPointException 발생
                System.out.println("테스트 - fullname : " + t.getWithdrawAccount().getUser().getFullname());
            }
            System.out.println("테스트 : =============================");
        });

        // then
        assertThat(transactionListPS.get(3).getDepositAccountBalance()).isEqualTo(800L);
    }

    @DisplayName("출금 내역 조회 테스트")
    @Test
    void findTransactionList_withdraw_test() throws Exception {
        // given
        Long accountId = 2L;

        // when
        List<Transaction> transactionListPS =
                transactionRepository.findTransactionList(
                        accountId, "WITHDRAW", 0
                );
        transactionListPS.forEach((t) -> {
            System.out.println("테스트 - id: " + t.getId());
            System.out.println("테스트 - 금액 : " + t.getAmount());
            System.out.println("테스트 - 츨금 계좌 : " + t.getSender());
            System.out.println("테스트 - 입금 계좌 : " + t.getReceiver());
            System.out.println("테스트 - 출금계좌 잔액: " + t.getWithdrawAccountBalance());
            System.out.println("테스트 - 입금계좌 잔액 : " + t.getDepositAccountBalance());
            System.out.println("테스트 - 잔액 : " + t.getWithdrawAccount().getBalance());
            System.out.println("테스트 - fullname : " + t.getWithdrawAccount().getUser().getFullname());
            System.out.println("테스트 : =============================");
        });

        // then
        assertThat(transactionListPS.get(0).getWithdrawAccountBalance()).isEqualTo(1100L);
    }

    @DisplayName("입금 내역 조회 테스트")
    @Test
    void findTransactionList_deposit_test() throws Exception {
        // given
        Long accountId = 3L;

        // when
        List<Transaction> transactionListPS =
                transactionRepository.findTransactionList(
                        accountId, "DEPOSIT", 0
                );
        transactionListPS.forEach((t) -> {
            System.out.println("테스트 - id: " + t.getId());
            System.out.println("테스트 - 금액 : " + t.getAmount());
            System.out.println("테스트 - 츨금 계좌 : " + t.getSender());
            System.out.println("테스트 - 입금 계좌 : " + t.getReceiver());
            System.out.println("테스트 - 출금계좌 잔액: " + t.getWithdrawAccountBalance());
            System.out.println("테스트 - 입금계좌 잔액 : " + t.getDepositAccountBalance());
            System.out.println("테스트 : =============================");
        });

        // then
        assertThat(transactionListPS.get(0).getDepositAccountBalance()).isEqualTo(1100L);
    }


    @Test
    public void dataJpa_test1() {
        List<Transaction> transactionList = transactionRepository.findAll();
        transactionList.forEach((transaction) -> {
            System.out.println("테스트 : " + transaction.getId());
            System.out.println("테스트 : " + transaction.getSender());
            System.out.println("테스트 : " + transaction.getReceiver());
            System.out.println("테스트 : " + transaction.getClassify());
            System.out.println("테스트 : =============================");
        });
    }

    @Test
    public void dataJpa_test2() {
        List<Transaction> transactionList = transactionRepository.findAll();
        transactionList.forEach((transaction) -> {
            System.out.println("테스트 : " + transaction.getId());
            System.out.println("테스트 : " + transaction.getSender());
            System.out.println("테스트 : " + transaction.getReceiver());
            System.out.println("테스트 : " + transaction.getClassify());
            System.out.println("테스트 : =============================");
        });
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

    private void autoIncrementReset() {
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE account_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE transaction_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
