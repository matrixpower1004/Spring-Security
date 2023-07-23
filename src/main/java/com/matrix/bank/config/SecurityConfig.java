package com.matrix.bank.config;

import com.matrix.bank.config.jwt.JwtAuthenticationFilter;
import com.matrix.bank.domain.user.UserEnum;
import com.matrix.bank.util.CustomResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * author         : Jason Lee
 * date           : 2023-07-22
 * description    :
 */
//@Slf4j  // 이렇게 하면 나중에 Junit 테스트를 할 때 조금 문제가 생긴다.
@Configuration  // 설정파일로 Bean 을 등록
public class SecurityConfig {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Bean   // IoC 컨테이네어 BcryptPasswordEncoder() 객체가 등록된다.
    // 이 Bean은 @Configuration 이 붙어있는 @Bean만 작동을 한다. 다른곳에 @Bean을 붙인다고 해서 작동하지 않는다.
    public BCryptPasswordEncoder passwordEncoder() {
        log.debug("디버그 : BCryptPasswordEncoder Bean 등록됨");
        return new BCryptPasswordEncoder();
    }

    // JWT 필터 등록이 필요함
    // 필터 등록이 방법이 버전업 되면서 변경 되었다. 내부 클래스가 하나 필요함
    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            // 필터 등록은 여기서 하면 된다.
            // authenticationManager()가 없으면 강제 세션 로그인을 할 수 없다.
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager));
            super.configure(builder);
        }
    }


    // JWT 서버를 만들 예정!! Session 사용 안함.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug("디버그 : filterChain Bean 등록됨");
        http.headers().frameOptions().disable();    // ifame 허용 안함
        http.csrf().disable();  // csrf가 걸려있으면 post맨 작동 안함.
        /**
         * cors 는 자바스크립트 요청을 거부하는 것. Cross Origin Resource Sharing의 약자로 다른 서버에 있는 프로그램중에
         * 자바스크립트로 요청되는 것들을 막는 옵션이라 우리 서버에 API 호출이 가능하도록 설정해야 한다.
         */
        http.cors().configurationSource(configurationSource()); // todo: cors 설정

        // jSessionId를 서버쪽에서 관리하지 않겠다는 뜻!!
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // react 나 앱에서 로그인 요청할 예정.
        http.formLogin().disable();

        // httpBasic은 브라우저가 팝업창을 이용해서 사용자 인증을 진행한다.
        http.httpBasic().disable();

        // 필터 적용
        http.apply(new CustomSecurityFilterManager());

        // Exception 가로채기
        http.exceptionHandling().authenticationEntryPoint(
                (request, response, authException) -> {
                    String uri = request.getRequestURI();
                    log.debug("디버그 : uri = {}", uri);
                    CustomResponseUtil.unAuthentication(response, "로그인을 해주세요.");
                });

        http.authorizeRequests()
                .antMatchers("/api/s/**").authenticated()
                .antMatchers("/api/admin/**").hasRole("" + UserEnum.ADMIN) // 최근 공식문서에서는 ROLE_ 안 붙여도 된다.
                .anyRequest().permitAll();
        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        log.debug("디버그 : configurationSource cors 설정이 SecurityFilterChain에 등록됨");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");    // 모든 Header 요청을 허용
        configuration.addAllowedMethod("*");    // GET. POST, PUT, DELETE (Javascript 요청 허용)
        configuration.addAllowedOriginPattern("*.*");   // 모든 IP 주소 허용 (프론트엔드 IP만 허용, react)
        configuration.setAllowCredentials(true);    // 클라이언트에서 쿠키 요청 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 주소 요청에 위의 설정을 넣어주겠다는 것
        return source;
    }
}
