package com.matrix.bank.dto.user;

import com.matrix.bank.domain.user.User;
import com.matrix.bank.util.CustomDateUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
public class UserRespDto {

    @Getter
    @Setter
    public static class LoginRespDto {
        private Long id;
        private String username;
        private String createdAt; // 응답할 때 날짜는 문자열로 내려줄 예정

        public LoginRespDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.createdAt = CustomDateUtil.toStringFormat(user.getCreatedAt());
        }
    }

    @ToString
    @Getter
    @Setter
    public static class JoinRespDto {
        private Long id;
        private String username;
        private String fullname;

        public JoinRespDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullname = user.getFullname();
        }
    }
}
