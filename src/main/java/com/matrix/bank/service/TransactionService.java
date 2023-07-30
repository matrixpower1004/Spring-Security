package com.matrix.bank.service;

import com.matrix.bank.domain.account.Account;
import com.matrix.bank.domain.account.AccountRepository;
import com.matrix.bank.domain.transaction.Transaction;
import com.matrix.bank.domain.transaction.TransactionRepository;
import com.matrix.bank.handler.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.matrix.bank.dto.transaction.TransactionRespDto.TransactionListRespDto;

/**
 * author         : Jason Lee
 * date           : 2023-07-29
 * description    :
 */
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionListRespDto findTransactionList(Long userId, Long accountNumber, String classify, int page) {
        // account가 존재하는지 확인
        Account accountPS = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new CustomApiException("해당 계좌를 찾을 수 없습니다."));

        // 해당 계좌의 소유자가 맞는지 확인
        accountPS.checkOwner(userId);

        // 출금내역 조회
        List<Transaction> transactionListPS = transactionRepository.findTransactionList(
                accountPS.getId(), classify, page);

        return new TransactionListRespDto(transactionListPS, accountPS);
    }

}
