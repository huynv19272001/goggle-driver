package com.fm.api.utils;


public class ContractNameOrder {
    public static String orderCode = "^((([A-Z]|[a-z]|[0-9]){2})(-|_|)).*";

    public static String contractName(String orderCode) {
        /*return "HD" + new DecimalFormat("000000").format(new Random().nextInt(999999));*/
        return "HD" + orderCode;
    }
}
