package com.matrix.bank.web;

import com.matrix.bank.config.auth.LoginUser;
import com.matrix.bank.dto.ResponseDto;
import com.matrix.bank.dto.transaction.TransactionRespDto;
import com.matrix.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.matrix.bank.dto.transaction.TransactionRespDto.*;

/**
 * author         : Jason Lee
 * date           : 2023-07-30
 * description    :
 */
@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/s/account/{number}/transaction")
    public ResponseEntity<?> findTransactionList(
            @PathVariable Long number,
            @RequestParam(value="classify", defaultValue = "ALL") String classify,
            @RequestParam(value="page", defaultValue = "0") int page,
            @AuthenticationPrincipal LoginUser loginUser
    ) {
        TransactionListRespDto transactionListRespDto =
                transactionService.findTransactionList(
                        loginUser.getUser().getId(), number, classify, page
                );
//        return new ResponseEntity<>(new ResponseDto<>(
//                1, "입출금목록보기 성공", transactionListRespDto),
//                org.springframework.http.HttpStatus.OK);
        // 둘 중에 어떤 것을 써도 동작하는 것은 같다.
        return ResponseEntity.ok().body(new ResponseDto<>(
                1, "입출금목록보기 성공", transactionListRespDto));
    }
}
