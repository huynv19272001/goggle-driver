package com.fm.base.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateInputUser {
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$";

    private static final Pattern patternUserName = Pattern.compile(USERNAME_PATTERN);

    private  static  final String PHONE_NUMBER_PATTERN ="(\\+84|0)[0-9]{9}$";

    private static final Pattern patternPhoneNumber = Pattern.compile(PHONE_NUMBER_PATTERN);

    private static final String EMAIL_PATTERN =  "^[a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
    //[A-Za-z0-9-+]+(\.[_A-Za-z0-9-])
    private static final Pattern patternEmail = Pattern.compile(EMAIL_PATTERN);

    private static final String PASSWORD_PATTERN = "^[A-Za-z0-9]\\w{6,}$";

    private static final Pattern patternPassword = Pattern.compile(PASSWORD_PATTERN);

    private  static  final String NAME_PATTERN = "[A-Za-z]{1,32}";

    private static final Pattern namePattern = Pattern.compile(NAME_PATTERN);

    public static  boolean isNameValid(final String firstOrLastName)
    {
        Matcher matcher = namePattern.matcher(firstOrLastName);
        return matcher.matches();
    }
    public  static boolean isValidPassword(final  String password)
    {
        Matcher matcher = patternPassword.matcher(password);
        return matcher.matches();
    }
    public static boolean isValidUserName(final String userName) {
        Matcher matcher = patternUserName.matcher(userName);
        return matcher.matches();
    }
    public static  boolean isValidPhoneNumber(final String phoneNumber)
    {
        Matcher matcher = patternPhoneNumber.matcher(phoneNumber.trim());
        return matcher.matches();
    }
    public static  boolean isValidEmail(final String email){
        Matcher matcher =  patternEmail.matcher(email);
        return matcher.matches();
    }

}
