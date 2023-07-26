package com.matrix.bank.service;

import com.matrix.bank.domain.account.Account;
import com.matrix.bank.domain.account.AccountRepository;
import com.matrix.bank.domain.user.User;
import com.matrix.bank.domain.user.UserRepository;
import com.matrix.bank.handler.ex.CustomApiException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.matrix.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import static com.matrix.bank.dto.account.AccountRespDto.AccountSaveRespDto;

/**
 * author         : Jason Lee
 * date           : 2023-07-25
 * description    :
 */
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public AccountSaveRespDto createAccount(AccountSaveReqDto accountSaveReqDto, Long userId) {
        // User 로그인 되어 있는 상태. User 로그인 되어 있는지 상태 체크는 Controller 의 역할.
        // 1. User DB에 있는지 먼저 검증 겸 User entity 가져오기
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다. id = " + userId));

        // 2. 햬당 계좌가 DB에 있는지 중복 여부 체크
        Optional<Account> accountOP = accountRepository.findByNumber(accountSaveReqDto.getNumber());
        if (accountOP.isPresent()) {
            throw new CustomApiException("이미 존재하는 계좌입니다.");
        }

        // 3. 계좌 생성
        Account accountPS = accountRepository.save(accountSaveReqDto.toEntity(userPS));

        // 4. DTO를 응답
        return new AccountSaveRespDto(accountPS);
    }

    public AccountListRespDto viewAccountListByUser(Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다. id = " + userId));

        // 유저의 모든 계좌 목록
        List<Account> accountListPS = accountRepository.findByUser_id(userId);
        // 이걸 return 하려면 Dto가 필요하다.
        return new AccountListRespDto(userPS, accountListPS);
    }

    @Getter
    @Setter
    public static class AccountListRespDto {
        private String fullname;
        private List<AccountDto> accounts = new ArrayList<>();

        public AccountListRespDto(User user, List<Account> accounts) {
            this.fullname = user.getFullname();
            this.accounts = accounts.stream().map(AccountDto::new).collect(Collectors.toList());
        }

        // 서비스에서 entity를 controller로 바로 넘기지 않고 Dto로 바꿔서 응답함.
        @Getter
        @Setter
        public class AccountDto {
            private Long id;
            private Long number;
            private Long balance;

            // Entity 객체를 Dto로 옮기는 작업
            // Entity를 Controller로 넘겨서 응답을 하게되면 json으로 변환하기 위해 메시지 컨버터가 발동한다.
            // 이 때 모든 필드를 getter를 다 때려서 내가 원하지 않는 lazy 로딩이 발생할 수 있다.

            public AccountDto(Account account) {
                this.id = account.getId();
                this.number = account.getNumber();
                this.balance = account.getBalance();
            }
        }
    }
}
