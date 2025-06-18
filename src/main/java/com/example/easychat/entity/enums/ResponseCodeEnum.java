package com.example.easychat.entity.enums;

/**
 * 响应码枚举类，定义系统通用的响应码和消息
 */
public enum ResponseCodeEnum {
    // 成功
    CODE_200(200, "操作成功"),

    // 客户端错误
    CODE_400(400, "请求参数错误"),
    CODE_401(401, "未授权或登录过期"),
    CODE_403(403, "禁止访问"),
    CODE_404(404, "请求地址不存在"),

    // 服务端错误
    CODE_500(500, "服务器内部错误"),
    CODE_503(503, "服务不可用"),

    CODE_901(901, "登录超时"),

    CODE_902(902, "您不是对方的好友，请先发送好友申请！"),
    CODE_903(903, "您已经不在该群聊中!"),
    CODE_600(600,"方法错误" );

    // 响应码
    private final int code;

    // 响应消息
    private final String msg;

    /**
     * 枚举构造方法
     *
     * @param code 响应码
     * @param msg 响应消息
     */
    ResponseCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 获取响应码
     *
     * @return 响应码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取响应消息
     *
     * @return 响应消息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 根据响应码查找对应的枚举
     *
     * @param code 响应码
     * @return 对应的枚举，如果未找到则返回 null
     */
    public static ResponseCodeEnum getByCode(int code) {
        for (ResponseCodeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
