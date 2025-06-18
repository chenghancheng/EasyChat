package com.example.easychat.utils;

import com.example.easychat.entity.enums.UserContactTypeEnum;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Arrays;

public class StringTools {
    //生成随机数
    public static String getUserId() {
        return UserContactTypeEnum.USER.getPrefix() + getRandomNumber(11);
    }

    public static String getGroupId() {return UserContactTypeEnum.GROUP.getPrefix() + getRandomNumber(11);}
    public static String getRandomNumber(Integer count) {
        return RandomStringUtils.random(count,false,true);
    }
    public static String getRandomString(Integer count) {
        return RandomStringUtils.random(count, true, false);
    }

    public static String encodeMD5(String s) {
        String md5Str = DigestUtils.md5DigestAsHex(s.getBytes());
        return s.isEmpty()?null:md5Str;
    }

    public static boolean isEmpty(String str) {
        if (null == str || "".equals(str) || "null".equals(str) || "\u0000".equals(str)) return true;
        else if ("".equals(str.trim())) return true;
        return false;
    }

    public static String cleanHtmlTag(String content) {
        if (isEmpty(content)) return content;
        content = content.replace("<","&lt;");
        content = content.replace("\r\n", "<br>");
        content = content.replace("\n", "<br>");
        return content;
    }

    public static final String getChatSessionId4User(String[] userIds) {
        Arrays.sort(userIds);
        return encodeMD5(StringUtils.join(userIds, ""));
    }
    public static final String getChatSessionId4Group(String groupId) {
        return encodeMD5(groupId);
    }
}
