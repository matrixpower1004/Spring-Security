package com.matrix.bank.dto.user;

import com.matrix.bank.domain.user.User;
import lombok.Getter;
import lombok.Setter;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
public class UserRespDto {


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
