package com.module.trimvideo.utils;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class Utils {

    public static String convertSize(long size) {
        double result = size / 1024;
        if (result < 1024) return formatCurrency(result) + "Kb";
        result = result / 1024;
        if (result < 1024) return formatCurrency(result) + "Mb";
        result = result / 1024;
        return formatCurrency(result) +"Gb";
    }

    public static String formatCurrency(double value) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ROOT);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        return numberFormat.format(value);
    }
}
