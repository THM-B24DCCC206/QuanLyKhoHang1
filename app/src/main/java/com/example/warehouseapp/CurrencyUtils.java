package com.example.warehouseapp;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public final class CurrencyUtils {

    private static final DecimalFormat VND_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        VND_FORMAT = new DecimalFormat("#,###", symbols);
    }

    private CurrencyUtils() {
    }

    public static String formatVnd(long amount) {
        return VND_FORMAT.format(amount) + " đ";
    }
}
