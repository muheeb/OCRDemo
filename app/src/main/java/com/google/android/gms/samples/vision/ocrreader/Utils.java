package com.google.android.gms.samples.vision.ocrreader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static boolean validateDate(String dateString) {
        String regex = "(\\d{2}/\\d{2}/\\d{4})";
        Matcher m = Pattern.compile(regex).matcher(dateString);
        if (m.find()) {
            Date date = null;
            try {
                date = new SimpleDateFormat("dd/MM/yyyy").parse(m.group(1));
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean validateCadeNumber(String cardNumber) {
        String regex = "(\\d{10})";
        Matcher m = Pattern.compile(regex).matcher(cardNumber);
        if (m.find()) {
            return true;
        } else {
            return false;
        }
    }
}
