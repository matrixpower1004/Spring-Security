package com.matrix.bank.config.auth;

import com.matrix.bank.domain.user.User;
import com.matrix.bank.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
@Service    // IoC 컨테이너에 띄워야 하기 때문에 꼭 붙여야 한다.
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    // UserRepository 의 findByUsername이 필요하다. (로그인 할 때 세션을 만들어 준다)
    private final UserRepository userRepository;

    // 시큐리티로 로그인이 될 때, 이 함수를 실행해서 username을 체크!!
    // 없으면 오류
    // 있으면 정상적으로 시큐리티 컨텍스트 내부 세션에 로그인된 세션이 만들어진다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userPS = userRepository.findByUsername(username).orElseThrow(
                // 인증을 하다가 터진 경우 시큐리티를 타고 있다면 제어권이 개발자에게 없다.
                // 그래서 오류가 발생하면 반드시 이 예외를던져서 제어를 해야한다.
                () -> new InternalAuthenticationServiceException("인증실패")
        );
        return new LoginUser(userPS);   // 시큐리티 세션에 유저 정보가 저장된다.
    }
}
