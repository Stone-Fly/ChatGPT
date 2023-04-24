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

    @ResponseBody
    @RequestMapping(value = "/ask", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String ask(String content) {
        Model model = new Model(new ArrayList<>());
        model.getMessages().add(new Model.Message(content));

        String result = HttpRequest.post(ChatConstant.CHAT_GPT_URL)
                .header(Header.AUTHORIZATION.getValue(), "Bearer " + ChatConstant.CHAT_GPT_KEY)
                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .setHttpProxy(ProxyConstant.PROXY_IP, ProxyConstant.PROXY_PORT)
                .body(JSONUtil.toJsonStr(model))
                .timeout(100000)
                .execute().body();
        ChatCompletion chatCompletion = JSONUtil.toBean(result, ChatCompletion.class);
        return chatCompletion.getChoices().get(0).getMessage().getContent();
    }

}
