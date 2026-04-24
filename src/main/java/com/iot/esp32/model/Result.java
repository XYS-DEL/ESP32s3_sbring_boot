package com.iot.esp32.model;

import lombok.Data;

/**
 * 统一 API 响应封装类
 * 格式：{ "code": 200, "msg": "操作成功", "data": [...] }
 */
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    // 成功时的快捷返回方法
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    // 失败时的快捷返回方法
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
}