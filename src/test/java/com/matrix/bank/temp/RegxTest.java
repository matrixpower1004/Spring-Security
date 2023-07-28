package com.matrix.bank.temp;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

/**
 * author         : Jason Lee
 * date           : 2023-07-23
 * description    :
 */
public class RegxTest {

    @Test
    void 한글만된다_test()  throws Exception {
        String value = "한글";
        boolean result = Pattern.matches("^[가-힣]+$", value);
        System.out.println("테스트 : " + result);
    }

    @Test
    void 한글은안된다_test() throws Exception {
        String value = "abc";
        boolean result = Pattern.matches("^[^ㄱ-ㅎ가-힣]*$", value);
        System.out.println("테스트 : " + result);
    }

    @Test
    void 영어만된다_test() throws Exception {
        String value = "a1bc";
        boolean result = Pattern.matches("^[a-zA-Z]+$", value);
        System.out.println("테스트 : " + result);
    }

    @Test
    void 영어는안된다_test() throws Exception {
        String value = "가2";
        boolean result = Pattern.matches("^[^a-zA-Z]*$", value);
        System.out.println("테스트 : " + result);
    }

    @Test
    void 영어와숫자만된다_test() throws Exception {
        String value = "a1bc";
        boolean result = Pattern.matches("^[a-zA-Z0-9]+$", value);
        System.out.println("테스트 : " + result);
    }

    @Test
    void 영어만되고_길이는최소2최대4이다_test() throws Exception {
        String value = "abc";
        boolean result = Pattern.matches("^[a-zA-Z]{2,4}$", value);
        System.out.println("테스트 : " + result);
    }

    // username, email, fullname 테스트
    @Test
    void user_username_test() {
        String username = "abc";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,20}$", username);
        System.out.println("테스트 : " + result);
    }

    @Test
    void user_fullname_test() {
        String fullname = "안녕";
        boolean result = Pattern.matches("^[a-zA-Z가-힣]{1,20}$", fullname);
        System.out.println("테스트 : " + result);
    }

    @Test
    void user_email_test() {
        String email = "matrixdsafdsafdsfa@nate.com";
        boolean result = Pattern.matches("^[a-zA-Z0-9]{2,6}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}", email);
        System.out.println("테스트 : " + result);

    }

    @Test
    void account_classify_test2() {
        String classify = "TRANSFER";
        boolean result = Pattern.matches("^(DEPOSIT|TRANSFER)$", classify);
        System.out.println("테스트 : " + result);
    }

    @Test
    void account_tel_test() {
        String tel = "01012345678";
        boolean result = Pattern.matches("[0-9]{3}[0-9]{3,4}[0-9]{4}", tel);
        System.out.println("테스트 : " + result);
    }

    @Test
    void account_tel_test2() {
        String tel = "023641234";
        boolean result = Pattern.matches("[0-9]{9,11}", tel);
        System.out.println("테스트 : " + result);
    }

}
