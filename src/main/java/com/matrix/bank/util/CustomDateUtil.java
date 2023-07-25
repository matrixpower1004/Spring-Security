package com.matrix.bank.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * author         : Jason Lee
 * date           : 2023-07-24
 * description    :
 */
public class CustomDateUtil {

    public static String toStringFormat(LocalDateTime localDateTime) {
        return localDateTime.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
    }
}
