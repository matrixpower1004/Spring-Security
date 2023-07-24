package com.matrix.bank.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrix.bank.dto.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
public class CustomResponseUtil {
    private static final Logger log = LoggerFactory.getLogger(CustomResponseUtil.class);

    public static void success(HttpServletResponse response, Object dto) {
        try {
            ObjectMapper om = new ObjectMapper();
            ResponseDto<?> responseDto = new ResponseDto<>(-1, "로그인 성공", dto);
            String responseBody = om.writeValueAsString(responseDto);
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(200);
            response.getWriter().println(responseBody);
        } catch (Exception e) {
            log.error("서버 파싱 에러");
        }
    }

    public static void fail(HttpServletResponse response, String message, HttpStatus httpStatus) {
        // 응답 데이터를 json으로 변환해서 보내야 하기 때문에 ObjectMapper가 필요하다.
        try {
            ObjectMapper om = new ObjectMapper();
            ResponseDto<?> responseDto = new ResponseDto<>(-1, message, null);

            // responseDto를 json으로 변환해서 응답한다.
            String responseBody = om.writeValueAsString(responseDto);
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(httpStatus.value());
            response.getWriter().println(responseBody); // 예쁘게 메시지를 포장하는 공통적인 응답 DTO를 만들어 보자!!
        } catch (Exception e) {
            log.error("서버 파싱 에러");
        }
    }
}
