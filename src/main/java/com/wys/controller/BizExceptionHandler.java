package com.wys.controller;

import com.wys.pojo.RestResult;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

/**
 * @program: demo_ssm-master
 * @description:
 * @author: syx
 * @create: 2019-08-09 11:49
 **/

@ControllerAdvice
public class BizExceptionHandler {

    /**
     * 用于处理通用异常
     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
//    public RestResult bindException(MethodArgumentNotValidException e) {
    public RestResult bindException(Exception e) {
//        BindingResult bindingResult = e.getBindingResult();
        String errorMesssage = "校验失败:";

//        for (FieldError fieldError : bindingResult.getFieldErrors()) {
//            errorMesssage += fieldError.getDefaultMessage() + ", ";
//        }

        return new RestResult("00000010", errorMesssage);
    }

//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody

}

