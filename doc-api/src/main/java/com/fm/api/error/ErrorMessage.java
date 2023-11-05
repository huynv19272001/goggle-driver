package com.fm.api.error;

import com.fm.base.models.sql.Order;
import com.fm.base.models.sql.OrderAction;

public class ErrorMessage {
    //User
    public final static String ORDER_CODE_MUST_ENTER_THE_CORRECT_FORMAT = "order code must enter the correct format";
    public final static String USER_ID_NOT_EXISTS = "User Id not exists!";
    public final static String USER_EXISTS = "User exists!";
    public final static String INVALID_USER_NAME = "User name must be contains 3-255 characters (a-zA-Z0-9 _ - ), not start with [ _ -] and [ _ -] can not appear consecutively";
    public final static String INVALID_PASSWORD = "Invalid password!";
    public final static String INVALID_EMAIL = "Invalid email!";
    public final static String INVALID_PHONE = "Invalid phone!";
    public final static String INVALID_BODY = "Invalid body!";
    public final static String INCORRECT_USERNAME_PASSWORD = "The Username or Password is Incorrect";

    public final static String PROJECT_ID_NOT_EXISTS = "Project Id not exists!";
    public final static String NAME_OR_PROJECT_ALREADY_EXISTS = "Name or code project already exists!";
    public final static String ORDER_DOES_NOT_EXISTS = "Order does not exists !";
    public final static String PLEASE_SEE_AGAIN = "only modified rows in state RECEIVED and PENDING!";
    public final static String PLEASE_SEE_AGAINS = "only modified rows in state RECEIVED and PENDING!";
    public final static String NOT_FOUND_ORDER_ID = "NOT FOUND ORDER iD !";
    public final static String NOT_FOUND_NOTE = "NOTE CANT NOT EMPTY  !";
    public final static String FILE_OPTION_MUST_BE_PDF = "file option must be pdf!";
    public final static String CAN_COUNT_THE_NUMBER_OF_PAGE = "can't count the number of pages!";
    public final static String PRICE_LIST_ID_DO_NOT_IN_PROJECT_ID = "priceListId do not in projectId!";
    public final static String NOT_FOUND_PROJECT_ID = "NOT FOUND PROJECT iD belong to user login!";
    public final static String NOT_FOUND_PROJECT_ID_ADMIN = "NOT FOUND PROJECT iD";
    public final static String FILE_DO_NOT_IN_ORDER = "FILE ID DO NOT ORDER ID!";
    public final static String PRICE_NAME_IS_EXISTS = "Price name is exits!";
    public final static String FILE_DO_NOT_EXIST= "file does not exist";
    public final static String FILE_ID_DO_NOT_EXIST= "file ids does not exist";
    public final static String THE_SPECIFIED_KEY_DOES_NOT_EXIST= "The specified key does not exist";
    public final static String PRICE_LIST_NOT_EXISTS = "Price list not exists!";
    public final static String INVALID_INPUT_DATE = "Invalid input date";
    public final static String TO_DATE_AND_FROM_DATE_NOT_FOUND = "toDATE and fromDate not found";
    public final static String MAXIMUM_IS_31DAYS = "Maximum statistical is 31 days ";
    public final static String START_TIME_NOT_BEFORE_END_TIME = "Start time not before end time";
    public final static String START_TIME_NOT_AFTER_DATE_NOW = "Start time not after date now";
    public final static String PRICE_LIST_ID_EXPIRE = "price list id expire";
    public final static String PRICE_LIST_CAN_NOT_LEFT_EMPTY = "price list cannot be left empty";
    public final static String ORDER_ID_NOT_EXITS = "Order id not exists";
    public final static String NOT_FOUND_PRICE_LIST_BY_ID = "Not found price list by id ";
    public final static String PRICE_LIST_OF_ORDER_IS_NOT_NULL = "Price list of order is not null ";
    public final static String MINIMUM_VALUE_ORDER_ID = "Order id has a minimum value of 1 ";
    public final static String CAN_NOT_PRICE_ORDER = "Can not price order & price order bigger 0";
    public final static String FILE_IS_NOT_TO_BLANK = "file is not to blank";
    public final static String NO_DELETE_ALL_FILE= "no delete all file";
    public final static String ORDER_CODE_ALREADY_EXIST= "order code already exist";
    public final static String PROJECT_ID_IS_NOT_TO_BLANK= "project Id is not to blank ";
    public final static String PRICE_LIST_ID_VALUE_UNIT_NO_NULL= "project Id value unit no null";
    public final static String ORDER_CODE_CANT_NOT_BE_EMPTY_AND_MUST_ENTER_ALL_EIGHT_CHARACTERS= "order code cannot be empty and must enter all 255 characters";
    public final static String ORDER_CODE_CANT_NOT_BE_EMPTY_AND_MUST_ENTER_ALL_FOUR_CHARACTERS= "order code cannot be empty and must enter all four characters";
    public final static String CAN_NOT_CANCEL_ORDER = "Can not cancel order";
    public final static String EXISTS_USER_NAME = "User name is exists !";
    public final static String FILE_UPLOAD_NO_SUCCESSFUL_25m = "file upload no SUCCESSFUL 25M!";
    public final static String EXISTS_EMAIL = "Email is exists !";
    public final static String EXISTS_PHONE_NUMBER = "Phone number is exists !";
    public final static String CAN_NOT_APPROVE_ORDER = "Can only approve orders with status 'PENDING'";
    public final static String ERROR_DATE_TIME_FORMAT= "Error date time format ";
    public final static String UNIT_NOT_NULL = "Price not null ,unit must be not null";
    public final static String PRICE_BIGGER_0 = "Price bigger 0 and can not be null";
    public final static String HAVE_NOT_PERMISSION = "You have not permission ";
    public final static String INVALID_OLD_PASSWORD = "Invalid old password ";
    public final static String FAILED_TO_CHANGE_PASSWORD = "Failed to change password ";


    public final static String HAVE_NOT_ORDER = "Have not orders";

    public static String createMessageUpdateOrderStatus(Order.Status statusCurrent, OrderAction.Type typeAction) {
        return "Order has a status is '" + statusCurrent + "'. Therefore, it is not possible to update the status of order with action '" + typeAction + "'";
    }

    public static String createMessageUpdateOrderStatus(String action) {
        return "Order is in status " + action + ". Therefore can't update order status!";
    }

    public final static String  STATUS_RECEIVED_OR_PRINTING = "Status must be RECEIVED or PRINTING ";

    public final static String  USER_ID_IS_NULL = " User id is null ";

    public final static String HAVE_NOT_PROJECT = "Have not project";
}
