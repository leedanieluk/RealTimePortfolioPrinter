package com.leedanieluk.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateUtilTest {
    @Test
    public void stringDateConvertsCorrectly() {
        assertTrue(DateUtil.parseDate("JAN-2020").equals(LocalDate.of(2020, 1, 31)));
    }
}
