package com.example.easychat.entity.vo;


//返回给前端的消息类
public class ResponseVO<T> {
    private String status;
    private Integer code;
    private String info;
    private T data;

    // 默认构造函数
    public ResponseVO() {}

    // 带参数的构造函数
    public ResponseVO(String status, Integer code, String info, T data) {
        this.status = status;
        this.code = code;
        this.info = info;
        this.data = data;
    }

    // 静态方法：成功的响应
    public static <T> ResponseVO<T> success(T data) {
        return new ResponseVO<T>("success", 200, "操作成功", data);
    }

    // 静态方法：失败的响应
    public static <T> ResponseVO<T> error(String info, T data) {
        return new ResponseVO<T>("error", 400, info, data);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
