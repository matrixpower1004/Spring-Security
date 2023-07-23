package com.matrix.bank.config.jwt;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */

/**
 * SECRET은 노출되면 안된다. (클라우드 AWS -환경변수, 파일에 있는 것을 읽을 수도 있다!!)
 * refresh token (x). 우리가 만들어 주는 토큰을 access token이라고 하는데 이 토큰이 만료되면 refresh token으로 access token 을 다시 생성하는 방법이 있다.
 * 그래서 자동 로그인을 계속 해주는 방법이 있지만 이 강의에서는 구현하지 않는다. 다른 수업을 굉장히 많이 해야 해서.
 */
public interface JwtVO {
    public static final String SECRET = "matrixb";  // HS256 (대칭키 암호화)
    public static final int EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 일주일 밀리초 * 초 * 분 * 시간 * 일
    public static final String TOKEN_PREFIX = "Bearer "; // 토큰 앞에 붙일 접두사, 프로토콜에 정의되어 있다.
    public static final String HEADER = "Authorization"; // 토큰을 실어 보낼 헤더의 이름
}
