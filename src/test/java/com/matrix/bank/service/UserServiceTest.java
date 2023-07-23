package com.matrix.bank.service;

import com.matrix.bank.config.dummy.DummyObject;
import com.matrix.bank.domain.user.User;
import com.matrix.bank.domain.user.UserRepository;
import com.matrix.bank.dto.user.UserReqDto.JoinReqDto;
import com.matrix.bank.dto.user.UserRespDto.JoinRespDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
// Spring 관련 Bean들이 하나도 없는 환경!!
@ExtendWith(MockitoExtension.class)
class UserServiceTest extends DummyObject {

    @InjectMocks    // @Mock과 @Spy에 선언된 객체를 주입받는다.
    private UserService userService;

    // 이건 실제로 띄울 필요가 없기 때문에 Mock으로 만들어준다.
    @Mock   // 가짜 객체를 주입해준다.
    private UserRepository userRepository;

    @Spy    // 실제 객체를 주입해준다.
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void 회원가입_test() {
        // Given
        JoinReqDto joinReqDto = new JoinReqDto();
        joinReqDto.setUsername("matrix");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("matrix@nate.com");
        joinReqDto.setFullname("매트릭스");

        // stub 1 - 가짜 환경에 method가 없기 때문에 stub이 필요하다.
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
//        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User())); // 일부러 Exception이 발생하도록 만든다.

        // stub 2
        User matrix = newMockUser(1L, "matrix", "매트릭스");
        when(userRepository.save(any())).thenReturn(matrix);  // 정상적인 User 객체를 리턴

        // When
        JoinRespDto joinRespDto = userService.userJoin(joinReqDto);
        System.out.println("테스트 : " + joinRespDto);

        // Then
        assertThat(joinRespDto.getId()).isEqualTo(1L);
        assertThat(joinRespDto.getUsername()).isEqualTo("matrix");

    }
}