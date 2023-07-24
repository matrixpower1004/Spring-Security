package com.matrix.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrix.bank.config.auth.LoginUser;
import com.matrix.bank.dto.user.UserRespDto.LoginRespDto;
import com.matrix.bank.util.CustomResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.matrix.bank.dto.user.UserReqDto.JoinReqDto.LoginReqDto;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setFilterProcessesUrl("/api/login");
        this.authenticationManager = authenticationManager;
    }

    // POST요청 : /login 할 때 동작.
    // /login 하면 request와 response가 인자로 들어온다. request 안에 들어잇는 json 데이터를 꺼내야 하기 때문에 ObjectMapper 가 필요함.
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.debug("디버그 : attemptAuthentication 호출됨");
        try {
            ObjectMapper om = new ObjectMapper();
            // 로그인 할 때는 username과 password만 받으면 된다. 따라서 Dto가 필요함.
            LoginReqDto loginReqDto = om.readValue(request.getInputStream(), LoginReqDto.class);

            // 강제 로그인을 위한 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            loginReqDto.getUsername(), loginReqDto.getPassword()
                    );

            // UserDetailsService의 loadUserByUsername() 호출
            // JWT를 쓴다 하더라도 컨트롤러 진입을 하면 시큐리티의 권한체크, 인증체크의 도움을 받을 수 있게 세션을 만든다.
            // 이 세션의 유효기간은 request하고, response 하면 끝.
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            return authentication;

        } catch (Exception e) {
            // ControllerAdvice로 넘길 수 없는 예외. 필터를 다 통과해야 Controller Layer로 넘어간다.
            // 이걸 처리하려면 예외를 하나 Override 해야 한다.
            // catch를 탔을 때 unsuccessfulAuthentication()을 호출함.
            throw new InternalAuthenticationServiceException(e.getMessage());
            // unsuccessfulAuthentication()를 직접 호출할 수도 있지만 InternalAuthenticationServiceException 을 던져서
            // 간접적으로 호출하는 것이 훨씬 코드가 깔끔해진다.
        }
    }

    // 로그인 실패시 처리
    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        // 로그인 실패시 응답을 내려줄 Dto도 하나 만들면 좋을 것 같다. 서버에서 내려주는 응답은 통잉설이 있어야 한다.
        CustomResponseUtil.fail(response, "로그인 실패", HttpStatus.UNAUTHORIZED);
    }

    // return authentication 잘 작동하면 successfulAuthentication 메서드가 호출된다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 여기까지 오면 로그인이 되어 세션이 만들어졌다는 의미다.
        log.debug("디버그 : successfulAuthentication 호출됨");

        LoginUser loginUser = (LoginUser) authResult.getPrincipal();
        String jwtToken = JwtProcess.create(loginUser);
        response.addHeader(JwtVO.HEADER, jwtToken);

        // 로그인이 되면 어떤 응답을 해줄지데 대한 Dto가 하나 필요하다.
        LoginRespDto loginRespDto = new LoginRespDto(loginUser.getUser());

        // 토큰을 헤더에 담아서 응답을 해주면 된다. 응답도 마찬가지로 Dto를 만들어서 응답한다.
        CustomResponseUtil.success(response, loginRespDto);
    }
}
