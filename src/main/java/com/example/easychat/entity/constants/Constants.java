package com.example.easychat.entity.constants;

public class Constants {
    public static final String REDIS_KEY_CHECK_CODE = "easychat:checkcode";

    public static final Integer REDIS_KEY_EXPIRES_HEART_BEAT = 6;
    public static final Integer REDIS_TIME_1MIN = 60;
    public static final Integer REDIS_TIME_1DAY = 60 * 60 * 24;

    public static final String REDIS_KEY_WS_USER_HEART_BEAT = "easychat:ws:user:heartbeat";
    public static final String REDIS_KEY_WS_TOKEN = "easychat:ws:token";

    public static final String REDIS_KEY_WS_TOKEN_USERID = "easychat:ws:token:userid";

    public static final Integer LENGTH_11 = 11;
    public static final Integer LENGTH_20 = 20;

    public static final String APPLY_INFO = "我是%s";


    //用户联系人列表
    public static final String REDIS_KEY_USER_CONTACT = "easychat:ws:user:contact:";
}
