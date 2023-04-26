package com.alageek.chatgpt.controller;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alageek.chatgpt.constant.ChatConstant;
import com.alageek.chatgpt.constant.ProxyConstant;
import com.alageek.chatgpt.dto.Model;
import com.alageek.chatgpt.dto.ChatCompletion;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/test")
public class TestController {

    @ResponseBody
    @RequestMapping(value = "/hello", produces = "application/json; charset=UTF-8")
    public String hello(String content) {
        return "Hello, " + content;
    }

}
