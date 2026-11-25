package com.example.warehouseapp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CurrencyUtilsTest {
    @Test
    public void formatVnd_usesDotThousandSeparators() {
        assertEquals("0 đ", CurrencyUtils.formatVnd(0));
        assertEquals("150.000 đ", CurrencyUtils.formatVnd(150000));
        assertEquals("5.350.000 đ", CurrencyUtils.formatVnd(5350000));
    }
}
