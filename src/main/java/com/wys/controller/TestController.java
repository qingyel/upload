package com.wys.controller;

import com.wys.pojo.ParamValidateVo;
import com.wys.pojo.Test;
import com.wys.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangyushuai@fang.com on 2018/4/19.
 */
@Controller
@RequestMapping("")
public class TestController {

    @Autowired
    TestService service;

    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public ModelAndView Index() {
        ModelAndView mv = new ModelAndView();
        List<Test> allUser =  service.getAllList();
        mv.setViewName("test");
        mv.addObject("userList",allUser);
        return mv;
    }

//    @RequestMapping(value = "validate")
//    public ParamValidateVo testParam(@RequestBody @Validated ParamValidateVo vo) {
//        System.out.println(vo);
//        Map<String, String> map = new HashMap<>();
//
//        return vo;
//    }
}
