package com.oms.common;

import lombok.Data;

/**
 * 统一响应结果封装
 */
@Data
public class R<T> {
    private int code;
    private String msg;
    private T data;

    private R() {}

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.code = 200;
        r.msg = "操作成功";
        r.data = data;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R<T> r = new R<>();
        r.code = 500;
        r.msg = msg;
        return r;
    }

    public static <T> R<T> error(int code, String msg) {
        R<T> r = new R<>();
        r.code = code;
        r.msg = msg;
        return r;
    }

    public static <T> R<T> unauthorized(String msg) {
        R<T> r = new R<>();
        r.code = 401;
        r.msg = msg;
        return r;
    }

    public static <T> R<T> forbidden(String msg) {
        R<T> r = new R<>();
        r.code = 403;
        r.msg = msg;
        return r;
    }
}
