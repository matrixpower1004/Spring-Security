package com.matrix.bank.config.dummy;

import com.matrix.bank.domain.account.Account;
import com.matrix.bank.domain.account.AccountRepository;
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
    CommandLineRunner init(UserRepository userRepository, AccountRepository accountRepository) {
        return (args) -> {
            // 서버 실행시 무조건 실행된다.
            User bank = userRepository.save(newUser("bank", "돈이좋아"));
            User matrix = userRepository.save(newUser("matrix", "매트릭스"));
            Account bankAccount = accountRepository.save(newAccount(1111L, bank));
            Account mindAccount = accountRepository.save(newAccount(2222L, matrix));
        };
    }
}
