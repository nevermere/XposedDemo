package com.android.xposed.model;

/**
 * 描述：
 *
 * @author fzJiang
 * @date 2020-12-08 17:44.
 */
public class BaseModel<T> {

    public static final int SUCCESS = 0;
    public static final int FAILED = 1;

    private int code;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
