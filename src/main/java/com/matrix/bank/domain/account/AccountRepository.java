package com.matrix.bank.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    // 여기에 대한 쿼리가 필요하지만 자동으로 생성된다. JPA query method
    // select * from account where number = :number
    // 신경 안 써도 됨 : 계좌 소유자 확인시에 쿼리가 두번 나가기 때문에 join fetch을 해야 하나?) - account.getUser().getId()
    // join fetch를 하면 조인해서 객체의 값을 미리 가져올 수 있다.
//    @Query("SELECT ac FROM Account ac JOIN FETCH ac.user u WHERE ac.number = :number")
    Optional<Account> findByNumber(Long number);

    // jpa query method
    // select * from account where user_id = :id
    List<Account> findByUser_id(Long id);
}
