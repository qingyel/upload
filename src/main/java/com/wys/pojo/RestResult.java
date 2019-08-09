

package com.wys.pojo;

import lombok.Data;

@Data
public class RestResult<T> {
    private static final long serialVersionUID = 4840778423101210973L;
    private String code;
    private String message;
    private T data;
    public static final String RESULT_SUCCESS = "0";
    public static final String RESULT_FAILD = "1";
    public static final String RESULT_RETRY = "2";
    public static final String RESULT_WARN = "3";
    public static final String RESULT_ERROR = "999";

    public RestResult() {
        this.code = "0";
        this.message = "success";
    }

    public RestResult(T data) {
        this.code = "0";
        this.message = "success";
        this.data = data;
    }

    public RestResult(String code, String message) {
        this.code = code;
        this.message = message;
    }


    @Override
    public String toString() {
        return "code=" + this.code + ", message=" + this.message + ", data class is " + (this.data != null ? this.data.getClass().getName() : null);
    }
}
