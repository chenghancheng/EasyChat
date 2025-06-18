package com.example.easychat.exception;

import com.example.easychat.entity.enums.ResponseCodeEnum;

public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    // 错误码
    private Integer code;

    /**
     * 构造方法 - 使用错误消息
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * 构造方法 - 使用错误消息和错误码
     * @param message 错误消息
     * @param code 错误码
     */
    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    /**
     * 构造方法 - 使用错误消息和异常
     * @param message 错误消息
     * @param cause 原始异常
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造方法 - 使用错误消息、错误码和异常
     * @param message 错误消息
     * @param code 错误码
     * @param cause 原始异常
     */
    public BusinessException(String message, Integer code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BusinessException(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum.getMsg());
        this.code = responseCodeEnum.getCode();
    }

    /**
     * 获取错误码
     * @return 错误码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置错误码
     * @param code 错误码
     */
    public void setCode(Integer code) {
        this.code = code;
    }
}
