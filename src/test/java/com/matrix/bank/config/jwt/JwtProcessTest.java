package com.matrix.bank.config.jwt;

import com.matrix.bank.config.auth.LoginUser;
import com.matrix.bank.domain.user.User;
import com.matrix.bank.domain.user.UserEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * author         : Jason Lee
 * date           : 2023-07-25
 * description    :
 */
class JwtProcessTest {

    private String createToken(Long id, UserEnum role) {
        User user = User.builder()
                .id(id)
                .role(role)
                .build();
        LoginUser loginUser = new LoginUser(user);
        return JwtProcess.create(loginUser);
    }

    @Test
    void create_test() {
        // Given

        // When
        String jwtToken = createToken(1L, UserEnum.CUSTOMER);
        System.out.println("테스트 : " + jwtToken);

        // Then
        // 새성된 토큰값은 계속 바뀌기 때문에 constant 값으로 테스트할 수 는 없다.
        assertTrue((jwtToken.startsWith(JwtVO.TOKEN_PREFIX))); // 토큰 생성시 앞에 접두사 잘 붙는지 테스트
    }

    @Test
    void verity_test() {
        // Given
        String token = createToken(2L, UserEnum.CUSTOMER); // Bearer 제거해서 처리하기
        String jwtToken = token.replace(JwtVO.TOKEN_PREFIX, "");

        // When
        LoginUser user = JwtProcess.verify(jwtToken);
        System.out.println("테스트 customer : " + user.getUser().getId());
        System.out.println("테스트 customer : " + user.getUser().getRole().name());

        // Then
        assertThat(user.getUser().getId()).isEqualTo(2L);
    }

}
