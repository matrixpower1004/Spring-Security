package com.matrix.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

}
