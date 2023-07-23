package com.matrix.bank.service;

import com.matrix.bank.domain.user.User;
import com.matrix.bank.domain.user.UserRepository;
import com.matrix.bank.dto.user.UserReqDto.JoinReqDto;
import com.matrix.bank.dto.user.UserRespDto.JoinRespDto;
import com.matrix.bank.handler.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 서비스는 Dto를 요청받고, Dto로 응답한다.
    @Transactional
    public JoinRespDto joinUser(JoinReqDto joinReqDto) {
        // 1. 동일 유저 네임 존해 검사
        Optional<User> userOp =  userRepository.findByUsername(joinReqDto.getUsername());
        if (userOp.isPresent()) {
            // 동일 유저 네임 존재한다는 뜻
            throw new CustomApiException("동일한 username이 존재합니다.");
        }

        // 2. 패스워드 인코딩 + 회원가입
        User userPS = userRepository.save(joinReqDto.toEntity(passwordEncoder));

        // 3. Dto 응답
        JoinRespDto joinRespDto = new JoinRespDto(userPS);
        return joinRespDto;
    }

}
