package com.matrix.bank.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
public interface UserRepository extends JpaRepository<User, Long> {
    // select * from user where username = ?
    Optional<User> findByUsername(String username); // Jpa NamedQuery 작동

    // save - 이미 만들어져 있음. (JpaRepository가 제공하는 기본 메서드)

}
