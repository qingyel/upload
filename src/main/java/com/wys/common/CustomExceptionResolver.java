package com.wys.common;

import com.alibaba.fastjson.JSON;
import com.wys.pojo.RestResult;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: ssm
 * @description:
 * @author: syx
 * @create: 2019-08-05 16:13
 **/
@Order(-100)
public class CustomExceptionResolver implements HandlerExceptionResolver {
    Logger logger = LoggerFactory.getLogger(CustomExceptionResolver.class);
        /**
         * 系统抛出的异常
         * @param httpServletRequest
         * @param response
         * @param o
         * @param e 系统抛出的异常
         * @return
         */
        @Override
        public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o, Exception e) {
            // 解析出异常类型
//            BusinessException customException = null;
//            // 若该异常类型是系统自定义的异常，直接取出异常信息在错误页面展示即可。
//            if(e instanceof BusinessException){
//                customException = (BusinessException)e;
//            }else{
//                // 如果不是系统自定义异常，构造一个系统自定义的异常类型，信息为“未知错误”
//                customException = new BusinessException("9999","未知错误");
//            }
//            //错误信息
//            String message = customException.getMessage();
            ModelAndView modelAndView = new ModelAndView();
            //将错误信息传到页面
//            modelAndView.addObject("message",message);
            //指向错误页面
            modelAndView.setViewName("error");
            RestResult result = new RestResult();
            result.setCode("1");
            result.setMessage("error");
            result.setData("");

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache, must-revalidate");
            try {
                response.getWriter().write(JSON.toJSONString(result));
            } catch (IOException ex) {
                logger.error("与客户端通讯异常：" + ex.getMessage(), ex);
                ex.printStackTrace();
            }
            return modelAndView;
        }
    }

