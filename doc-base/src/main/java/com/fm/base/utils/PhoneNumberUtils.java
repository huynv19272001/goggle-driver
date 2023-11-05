package com.fm.base.utils;

import java.util.Optional;
import java.util.regex.Pattern;

public class PhoneNumberUtils {
    public static Optional<String> formatPhoneNumber(String input){
        Pattern p = Pattern.compile("[^0-9]");
        String numericOutput = p.matcher(input).replaceAll("");
        if(numericOutput.length() < 10) {
            return Optional.empty();
        }
        return Optional.of("+1" + numericOutput.substring(numericOutput.length() - 10));
    }
}
