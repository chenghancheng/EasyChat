package com.example.easychat.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.easychat.entity.enums.ResponseCodeEnum;
import com.example.easychat.exception.BusinessException;

import java.util.List;

public class JsonUtils {
    public static SerializerFeature[] FEATURES = new SerializerFeature[]{SerializerFeature.WriteMapNullValue};

    public static String convertObj2Json(Object obj) {return JSON.toJSONString(obj, FEATURES);}

    public static <T> T convertJson2Obj(String json, Class<T> classz) {
        try {
            return JSONObject.parseObject(json, classz);
        } catch (Exception e) {
            throw new BusinessException("convetJson2Obj异常,json:" + json);
        }
    }

    public static <T> List<T> convertJsonArray2List(String json, Class<T> classz) {
        try {
            return JSONArray.parseArray(json, classz);
        } catch (Exception e) {
            throw new BusinessException("convertJsonArray2List异常,json:" + json);
        }
    }
}
