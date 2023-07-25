package com.matrix.bank.config.jwt;

/**
 * author         : Jason Lee
 * date           : 2023-07-24
 * description    :
 */

import com.matrix.bank.config.auth.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 모든 주소에서 동작함 (토큰 검증)
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 1. 헤더를 검증해야 한다.
        if (isHeaderVerify(request, response)) {
            // 토큰이 존재함
            log.debug("디버그 : 토큰이 존재함" );

            String token = request.getHeader(JwtVO.HEADER).replace(JwtVO.TOKEN_PREFIX, "");
            LoginUser loginUser = JwtProcess.verify(token);
            log.debug("디버그 : 토큰 검증이 완료됨" );

            // 임시 세션을 만든다 (UserDetails or username)
            // 여기서 핵심은 user role만 잘 들어가 있으면 된다.
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    loginUser, null, loginUser.getAuthorities()); // id 와 role만 존재
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("디버그 : 임시 세션이 생성됨" );
        }
        chain.doFilter(request, response);
    }

    private boolean isHeaderVerify(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader(JwtVO.HEADER);
        if (header == null || !header.startsWith(JwtVO.TOKEN_PREFIX)) {
            return false;
        }
        return true;
    }
}
