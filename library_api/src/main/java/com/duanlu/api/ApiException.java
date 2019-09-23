package com.duanlu.api;

/**
 * Created by 段露 on 2018/11/15 20:16.
 *
 * @author 段露
 * @version 1.0.0
 * @class ApiException
 * @describe API请求异常基类.
 */
public class ApiException extends Exception {

    private int code;
    private String message;

    public ApiException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
