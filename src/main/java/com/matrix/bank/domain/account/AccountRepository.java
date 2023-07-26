package com.matrix.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * 여기에 대한 쿼리가 필요하지만 자동으로 생성된다. JPA query method
     * select * from account where number = :number
     */
    //todo: 리팩토링 예정!! (계좌 소유자 확인시에 쿼리가 두번 나가는 것을 수정해아 함. join fetch)
    Optional<Account> findByNumber(Long number);
}
