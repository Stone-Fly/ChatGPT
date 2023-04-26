package com.alageek.chatgpt;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alageek.chatgpt.constant.ChatConstant;
import com.alageek.chatgpt.constant.ProxyConstant;
import com.alageek.chatgpt.dto.Model;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
class ChatGptApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void getModelList() {
        String result = HttpRequest.get("https://api.openai.com/v1/models")
                .header(Header.AUTHORIZATION.getValue(), "Bearer " + ChatConstant.CHAT_GPT_KEY)
                .setHttpProxy(ProxyConstant.PROXY_IP, ProxyConstant.PROXY_PORT)
                .execute().body();
        System.out.println(result);
    }

    @Test
    void getModelInfo() {
        String model = "gpt-3.5-turbo";
        String result = HttpRequest.get("https://api.openai.com/v1/models/" + model)
                .header(Header.AUTHORIZATION.getValue(), "Bearer " + ChatConstant.CHAT_GPT_KEY)
                .setHttpProxy(ProxyConstant.PROXY_IP, ProxyConstant.PROXY_PORT)
                .execute().body();
        System.out.println(result);
    }

    @Test
    void createChatCompletion() {
        Model model = new Model(new ArrayList<>());
        model.getMessages().add(new Model.Message("你好"));
        String result = HttpRequest.post(ChatConstant.CHAT_GPT_URL)
                .header(Header.AUTHORIZATION.getValue(), "Bearer " + ChatConstant.CHAT_GPT_KEY)
                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .setHttpProxy(ProxyConstant.PROXY_IP, ProxyConstant.PROXY_PORT)
                .body(JSONUtil.toJsonStr(model))
                .timeout(100000)
                .execute().body();
        System.out.println(result);
    }

}
