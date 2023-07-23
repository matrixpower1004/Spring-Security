package com.matrix.bank.dto.user;

import com.matrix.bank.domain.user.User;
import com.matrix.bank.domain.user.UserEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
public class UserReqDto {

    @ToString
    @Getter
    @Setter
    public static class JoinReqDto {
        // 유효성 검사
        private String username;
        private String password;
        private String email;
        private String fullname;

        public User toEntity(BCryptPasswordEncoder passwordEncoder) {
            return User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .fullname(fullname)
                    .role(UserEnum.CUSTOMER)
                    .build();
        }
    }
}
