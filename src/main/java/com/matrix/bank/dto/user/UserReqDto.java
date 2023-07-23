package com.matrix.bank.dto.user;

import com.matrix.bank.domain.user.User;
import com.matrix.bank.domain.user.UserEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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

        @Getter
        @Setter
        public static class LoginReqDto {
            // Controller 레이어로 가기 전이라 여기서는 validation 체크를 하지 못한다.
            private String username;
            private String password;
        }
        // 영문, 숫자만 가능, 길이 최소 2~20자 이내, 한글 공백은 들어가면 안 된다.
        @Pattern(regexp = "^[a-zA-Z0-9]{2,20}$", message = "아이디는 영문, 숫자만 가능하며, 2~20자 이내로 입력해주세요.")
        @NotEmpty   // null이거나 공백일 수 없다.
        private String username;

        // 길이 4~20자 이내
        @NotEmpty
        @Size(min = 4, max = 20)    // size는 String type에만 사용 가능
        private String password;

        // 이메일 형식에 맞아야 한다. RFC5322 Email Validation 사용
        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "이메일 형식에 맞지 않습니다.")
        private String email;

        // 영어, 한글 가능. 1~20자 이내
        @NotEmpty
        @Pattern(regexp = "^[a-zA-Z가-힣]{1,20}$", message = "이름은 영어, 한글만 가능하며, 1~20자 이내로 입력해주세요.")
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
