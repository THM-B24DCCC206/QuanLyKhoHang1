package com.example.warehouseapp;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Lớp tiện ích định dạng số tiền sang chuẩn VNĐ với dấu chấm phân cách hàng nghìn.
 */
public final class CurrencyUtils {

    private static final DecimalFormat VND_FORMAT;

    static {
        // Cố định dấu phân cách hàng nghìn là dấu chấm bất kể ngôn ngữ thiết bị
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
