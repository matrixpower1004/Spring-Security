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

import static com.matrix.bank.dto.account.AccountReqDto.AccountSaveReqDto;
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
    // cos로 로그인을 했는데, cos의 id가 2번이에요!!
    // 이렇게 붙이면 권한 처리를 해야 하는 번거로움이 있어서 선호하지 않는다.

    // 인증이 필요하고, account 테이블 데이터 다 주세요!!

    // 인증이 필요하고, account 테이블에 login한 유저의 개인을 주세요.
    @GetMapping("/s/account/login-user")
    public ResponseEntity<?> findUserAccount(@AuthenticationPrincipal LoginUser loginUser) {

        AccountListRespDto accountListRespDto =
                accountService.viewAccountListByUser(loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1,
                "본인 계좌 목록보기 성공", accountListRespDto), HttpStatus.OK);
    }
}
