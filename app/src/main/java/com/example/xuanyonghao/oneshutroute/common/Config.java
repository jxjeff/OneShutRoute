package com.example.xuanyonghao.oneshutroute.common;

/**
 * Created by xuanyonghao on 2017/10/29.
 */

public class Config {
    public interface SMS_CONFIG {
        String SEND_PHONE_NUMBER = "10000";
        String SEND_CONTENT = "xykdmm";
//        String SEND_PHONE_NUMBER = "10010";
//        String SEND_CONTENT = "cxye";
        String SENT_SMS_ACTION = "SENT_SMS_ACTION";
    }
    public interface MAIN_HANDLE {
        int HANDLE_RECEIVED_SMS_CODE = 1;
        int HANDLE_HOST_USERNAME_RECEIVE = 2; 
        String HANDLE_MESSAGE_KEY = "message_key";
    }
    public interface LINK_CONFIG {
        String CONFIG_FILE_NAME = "config";
        String HOST_KEY = "host";
        String USERNAME_KEY = "username";
        String LOGIN_ROUTE_USERNAME_KEY = "routeUsername";
        String LOGIN_ROUTE_PASSWORD_KEY = "routePassword";
        String DEFAULT_VALUE = "";
    }
}
