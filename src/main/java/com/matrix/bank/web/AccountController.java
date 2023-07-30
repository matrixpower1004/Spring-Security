package com.matrix.bank.web;

import com.matrix.bank.config.auth.LoginUser;
import com.matrix.bank.dto.ResponseDto;
import com.matrix.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import com.matrix.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.matrix.bank.dto.account.AccountReqDto.*;
import static com.matrix.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import static com.matrix.bank.dto.account.AccountRespDto.*;
import static com.matrix.bank.dto.account.AccountRespDto.AccountListRespDto;

/**
 * author         : Jason Lee
 * date           : 2023-07-25
 * description    :
 */
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/s/account")
    public ResponseEntity<?> saveAccount(@RequestBody @Valid AccountSaveReqDto accountSaveReqDto,
                                         BindingResult bindingResult,
                                         @AuthenticationPrincipal LoginUser loginUser) {
        AccountSaveRespDto accountSaveRespDto = accountService.createAccount(
                accountSaveReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1,
                "계좌 등록 성공", accountSaveRespDto), HttpStatus.CREATED);
    }

    // 인증이 필요하고, account 테이블의 1번 row를 주세요!!
    // cos로 로그인을 했는데, cos의 id가 2번이에요!! (/s/account/2)
    // 이렇게 붙이면 권한 처리를 해야 하는 번거로움이 있어서 선호하지 않는다.

    // 인증이 필요하고, account 테이블의 데이터를 다 주세요!!

    // 인증이 필요하고, account 테이블에서 login한 유저의 개좌만 주세요.
    @GetMapping("/s/account/login-user")
    public ResponseEntity<?> findUserAccount(@AuthenticationPrincipal LoginUser loginUser) {

        AccountListRespDto accountListRespDto =
                accountService.viewAccountListByUser(loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1,
                "본인 계좌 목록보기 성공", accountListRespDto), HttpStatus.OK);
    }

    @DeleteMapping("/s/account/{number}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long number,
                                           @AuthenticationPrincipal LoginUser loginUser) {
        accountService.deleteAccount(number, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1,
                "계좌 삭제 성공", null), HttpStatus.OK);
    }

    @PostMapping("/account/deposit")
    public ResponseEntity<?> depositAccount(
            @RequestBody @Valid AccountDepositReqDto accountDepositReqDto,
            BindingResult bindingResult) {
        AccountDepositRespDto accountDepositRespDto = accountService.depositAccount(accountDepositReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 입금 완료", accountDepositRespDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/withdraw")
    public ResponseEntity<?> withdrawAccount(
            @RequestBody @Valid AccountWithdrawReqDto accountWithdrawReqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal LoginUser loginUser) {
        AccountWithdrawRespDto accountDepositRespDto = accountService.withdrawAccount(accountWithdrawReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 출금 완료", accountDepositRespDto), HttpStatus.CREATED);
    }

    @PostMapping("/s/account/transfer")
    public ResponseEntity<?> transferAccount(
            @RequestBody @Valid AccountTransferReqDto accountTransferReqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal LoginUser loginUser) {
        AccountTransferRespDto accountWithdrawRespDto = accountService.transferAccount(accountTransferReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 이체 완료", accountWithdrawRespDto), HttpStatus.CREATED);
    }

    @GetMapping("/s/account/{number}")
    public ResponseEntity<?> findDetailAccount(
            @PathVariable Long number,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @AuthenticationPrincipal LoginUser loginUser
    ) {
        AccountDetailRespDto accountDetailRespDto = accountService.viewAccountDetail(
                number, loginUser.getUser().getId(), page);
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌상세보기 성공", accountDetailRespDto), HttpStatus.OK);
    }
}
