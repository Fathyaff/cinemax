package com.example.demo.core.util;

import java.util.Objects;

/**
 * @author fathyaff
 * @date 15/08/21 00.28
 */
public final class StringUtils {

    private StringUtils() {}

    public static boolean isNullOrEmpty(String input) {
        return Objects.isNull(input) || "".equals(input);
    }
}
