package com.matrix.bank.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.matrix.bank.config.auth.LoginUser;
import com.matrix.bank.domain.user.User;
import com.matrix.bank.domain.user.UserEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
public class JwtProcess {
    private final Logger log = LoggerFactory.getLogger(getClass());

    // 토큰 생성
    public static String create(LoginUser loginUser) {
        String jwtToken = JWT.create()
                .withSubject("bank")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtVO.EXPIRATION_TIME)) // 현재 시간 + 만료시간. 토큰이 만들어지는 시점부터 7일뒤까지 유효하다.
                .withClaim("id", loginUser.getUser().getId())
                .withClaim("role", loginUser.getUser().getRole().toString())  // String으로 넣어야 한다.
                .sign(Algorithm.HMAC256(JwtVO.SECRET));
        return JwtVO.TOKEN_PREFIX + jwtToken;
    }

    // 토큰 검증 (return 되는 LoginUser 객체를 강제로 시큐리티 세션에 직접 주입할 예정)
    public static LoginUser verify(String token) {
        DecodedJWT decodedJWT = JWT.require((Algorithm.HMAC256(JwtVO.SECRET)))
                .build()
                .verify(token);
        Long id = decodedJWT.getClaim("id").asLong();
        String role = decodedJWT.getClaim("role").asString();
        // 토큰의 내용은 암호화가 되어 있지 않기 때문에 안에 있는 값을 다 열어볼 수 있다. 그래서 id 와 role 정도만 넣는 게 좋다.
        User user = User.builder().id(id).role(UserEnum.valueOf(role)).build();
        LoginUser loginUser = new LoginUser(user);
        return loginUser;
    }
}
