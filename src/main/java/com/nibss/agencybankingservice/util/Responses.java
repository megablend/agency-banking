/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nibss.agencybankingservice.util;

/**
 *
 * @author cmegafu
 */
public interface Responses {
    // Response Codes
    String SUCCESSFUL = "00";
    String INVALID_AGENT_REQUEST = "04";
    String INVALID_AGENT_REQUEST_NULL = "35";
    String INVALID_LGA = "05";
    String INVALID_SERVICE_PROVIDED = "07";
    String INVALID_BVN = "08";
    String INVALID_AGENT = "10";
    String INVALID_DATE_FORMAT = "11";
    String DATABASE_ERROR = "12";
    String TRANSACTION_REPORT_EXCEEDS_THRESHOLD = "13";
    String INVALID_TRANSACTION_REQUEST_BULK = "14";
    String INVALID_AMOUNT = "15";
    String INVALID_NUMBER = "16";
    String INVALID_TRANSACTION_REQUEST  = "17";
    String INVALID_TRANSACTION_SUMMARY_REQUEST = "18";
    String INVALID_RESET_REQUEST = "19";
    String INVALID_INSTITUTION = "20";
    String NO_SERVICES = "21";
    String SYSTEM_ERROR = "22";
    String UNABLE_TO_GENERATE_CREDENTIALS = "23";
    String INVALID_LATITUDE = "24";
    String INVALID_LONGITUDE = "25";
    String INVALID_TRANSACTION_MONTH = "26";
    String INVALID_TRANSACTION_DAY = "27";
    String PASSWORD_POLICY_VIOLATION = "28";
    String BVN_EXISTS = "29";
    String INVALID_EMAIL_ADDRESS = "30";
    String UNABLE_TO_GENERATE_CODE = "31";
    String AGENT_ALREADY_EXISTS = "32";
    String AGENT_DOES_NOT_EXIST = "33";
    String INVALID_AGENT_USERNAME = "34";
    String UNABLE_TO_CREATE_USER = "35";
    String UNABLE_TO_CREATE_AGENT = "36";
    String USER_EMAIL_EXISTS = "37";
}
