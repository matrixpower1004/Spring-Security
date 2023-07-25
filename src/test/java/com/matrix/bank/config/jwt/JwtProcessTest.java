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

    @Test
    void create_test() {
        // Given
        User user = User.builder().id(1L).role(UserEnum.ADMIN).build();
        LoginUser loginUser = new LoginUser(user);

        // When
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("테스트 : " + jwtToken);

        // Then
        // 새성된 토큰값은 계속 바뀌기 때문에 constant 값으로 테스트할 수 는 없다.
        assertTrue((jwtToken.startsWith(JwtVO.TOKEN_PREFIX))); // 토큰 생성시 앞에 접두사 잘 붙는지 테스트
    }

    @Test
    void verity_test() {
        // Given
        // Bearer 헤더를 붙이지 않는 이유는 doFilterInternal()에서 토큰 값을 가져올 때 JwrVO.HEADER 에 있는 Bearer 이라는 텍스트를 날리기 때문.
        String userJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJiYW5rIiwiZXhwIjoxNjkwODU2OTQ1LCJpZCI6MSwicm9sZSI6IkNVU1RPTUVSIn0.C1ezQJEvsNBBocaDvpyPLnzC5h9my-pGV57kfHDjO-w";
        String adminJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJiYW5rIiwiZXhwIjoxNjkwODU3ODA4LCJpZCI6MSwicm9sZSI6IkFETUlOIn0.huqzJe38zD2TbaIPvf-E_4otVR6miNWCFrFsy0YQn1E";

        // When
        LoginUser loginUser = JwtProcess.verify(userJwtToken);
        System.out.println("테스트 : " + loginUser.getUser().getId());
        System.out.println("테스트 : " + loginUser.getUser().getRole());

        LoginUser adminUser = JwtProcess.verify(adminJwtToken);
        System.out.println("테스트 : " + adminUser.getUser().getId());
        System.out.println("테스트 : " + adminUser.getUser().getRole());

        // Then
        assertThat(loginUser.getUser().getId()).isEqualTo(1L);
        assertThat(adminUser.getUser().getRole()).isEqualTo(UserEnum.ADMIN);
    }

}
