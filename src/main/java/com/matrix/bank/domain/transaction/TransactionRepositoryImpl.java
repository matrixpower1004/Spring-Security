package com.matrix.bank.domain.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * author         : Jason Lee
 * date           : 2023-07-29
 * description    :
 */
interface Dao {
    // 받아야 할 파라미터가 하나라면 @Param을 생략해도 되지만, 여러개라면 꼭 붙여줘야 한다.
    List<Transaction> findTransactionList(
            @Param("accountId") Long accountId,
            @Param("classify") String classify,
            @Param("page") Integer page
    );
}

// Impl은 꼭 붙여줘야 하고, 기존 Interface 이름이 앞에 붙어야 한다.
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements Dao {

    private final EntityManager em;

    @Override
    public List<Transaction> findTransactionList(Long accountId, String classify, Integer page) {
        // 동적쿼리 (classify 값으로 동적쿼리 연동 = DEPOSIT, WITHDRAW, ALL)
        // JPQL 문법
        String sql = "";
        sql += "SELECT t FROM Transaction t ";

        if (classify.equals("WITHDRAW")) {
            sql += "JOIN FETCH t.withdrawAccount wa ";
            sql += "WHERE t.withdrawAccount.id = :withdrawAccountId";
        } else if (classify.equals("DEPOSIT")) {
            sql += "JOIN FETCH t.depositAccount da ";
            sql += "WHERE t.depositAccount.id = :depositAccountId";
        } else { // ALL
            sql += "LEFT JOIN t.withdrawAccount wa ";
            sql += "LEFT JOIN t.depositAccount da ";
            sql += "WHERE t.withdrawAccount.id = :withdrawAccountId ";
            sql += "OR ";
            sql += "t.depositAccount.id = :depositAccountId";
        }

        TypedQuery<Transaction> query = em.createQuery(sql, Transaction.class);

        if (classify.equals("WITHDRAW")) {
            query.setParameter("withdrawAccountId", accountId);
        } else if (classify.equals("DEPOSIT")) {
            query.setParameter("depositAccountId", accountId);
        } else { // ALL
            query.setParameter("withdrawAccountId", accountId);
            query.setParameter("depositAccountId", accountId);
        }

        query.setFirstResult(page * 5); // 5, 10, 15
        query.setMaxResults(5);

        return query.getResultList(); // 한건이 아니기 때문에 getResultList() 사용
    }
}
