package com.matrix.bank.temp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * author         : Jason Lee
 * date           : 2023-07-28
 * description    :
 */
public class LongTest {

    @DisplayName("Boxed primitive Long type의 동등비교(==)는 .longValue()를 붙여야 한다.")
    @Test
    void long_test() throws Exception {
        // given
        Long number1 = 1111L;
        Long number2 = 1111L;

        // when
        if (number1.longValue() == number2.longValue()) {
            System.out.printf("테스트 - number1: %d == number2: %d 는 동일합니다.%n", number1, number2);
        }

        // then
    }

    @DisplayName("크고 작다는 .longvalue()를 안 붙여도 비교가 가능하다.")
    @Test
    void long_test2() throws Exception {
        // given
        Long amount1 = 100L;
        Long acount2 = 1000L;

        // when
        if (amount1 < acount2) {
            System.out.printf("테스트 - amount1: %d < amount2: %d 는 amount1이 작습니다.%n", amount1, acount2);
        }

        // then
    }

    @DisplayName("2^8 = 256(-128 ~ + 127)의 범위내에서는 == 비교가 가능하다.")
    @Test
    void long_test3() throws Exception {
        // given (2^8 = 256 범위 : -128 ~ + 127까지는 cache가 되어있어서 == 비교가 가능하다)
        Long v1 = 127L;
        Long v2 = 127L;

        // when
        if (v1 == v2) {
            System.out.printf("테스트 - v1: %d 과 v2: %d 는 같습니다.%n", v1, v2);
        }

        // then
    }

    @DisplayName("Long은 Boxed tpye 이므로 .equals()로 동등 비교가 가능하다.")
    @Test
    void long_test4() throws Exception {
        // given
        Long v1 = 1000L;
        Long v2 = 1000L;

        // when
        if (v1.equals(v2)) {
            System.out.printf("테스트 - v1: %d 과 v2: %d 는 같습니다.%n", v1, v2);
        }

        // then
    }
}
