package com.duanlu.api;

import java.io.Serializable;

/**
 * Created by 段露 on 2017/09/06 14:29.
 *
 * @author 段露
 * @version 1.0.0
 * @class ResultBean
 * @describe 基础返回实体.
 */
public class ResultBean<T> implements State, Serializable {

    private int code;//一般200表示成功
    private String message;//状态描述.
    private T data;//数据.
    private int systemTime;//系统时间.

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public int getSystemTime() {
        return systemTime;
    }

    @Override
    public boolean isSuccess() {
        return State.DEFAULT_RESULT_SUCCEED_CODE == code;
    }

}
