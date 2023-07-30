package com.matrix.bank.config.dummy;

import com.matrix.bank.domain.account.Account;
import com.matrix.bank.domain.account.AccountRepository;
import com.matrix.bank.domain.transaction.Transaction;
import com.matrix.bank.domain.transaction.TransactionRepository;
import com.matrix.bank.domain.user.User;
import com.matrix.bank.domain.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * author         : Jason Lee
 * date           : 2023-07-25
 * description    :
 */
@Configuration
public class DummyDevInit extends DummyObject {

    @Profile("dev") // prod 환경에서는 실행하면 안 된다.
    @Bean
    CommandLineRunner init(
            UserRepository userRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository
    ) {
        return (args) -> {
            // 서버 실행시 무조건 실행된다.
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
        };
    }
}
