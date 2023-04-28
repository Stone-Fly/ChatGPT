package com.alageek.chatgpt.service;

import cn.hutool.core.convert.ConvertException;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.alageek.chatgpt.cache.SseEmitterCache;
import com.alageek.chatgpt.constant.ChatConstant;
import com.alageek.chatgpt.constant.ProxyConstant;
import com.alageek.chatgpt.dto.AskReq;
import com.alageek.chatgpt.dto.ChatCompletion;
import com.alageek.chatgpt.dto.Model;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

@Slf4j
@Service
public class ChatService {

    @Resource
    private RestTemplate restTemplate;

    public void chat(AskReq askReq, SseEmitter sseEmitter) {
        Model model = new Model(new ArrayList<>());
        model.setStream(true);
        model.getMessages().add(new Model.Message(Model.Message.ROLE_USER, askReq.getQuestion()));

        // 创建 HttpHeaders，添加 header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(Header.AUTHORIZATION.getValue(), "Bearer " + askReq.getKey());

        // 创建 HttpEntity，添加 body
        HttpEntity<String> requestEntity = new HttpEntity<>(JSONUtil.toJsonStr(model), headers);

        // 自定义 RequestCallback，设置 header 和 body
        RequestCallback requestCallback = request -> {
            HttpHeaders httpHeaders = requestEntity.getHeaders();
            request.getHeaders().putAll(httpHeaders);
            if (requestEntity.getBody() != null) {
                restTemplate.getMessageConverters().forEach(converter -> {
                    if (converter instanceof org.springframework.http.converter.StringHttpMessageConverter && converter.canWrite(String.class, MediaType.APPLICATION_JSON)) {
                        try {
                            ((org.springframework.http.converter.StringHttpMessageConverter) converter).write(requestEntity.getBody(), MediaType.APPLICATION_JSON, request);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to write request body.", e);
                        }
                    }
                });
            }
        };

        // 自定义 ResponseExtractor，处理响应
        ResponseExtractor<Void> responseExtractor = response -> {
            InputStreamReader inputStreamReader = new InputStreamReader(response.getBody());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder buffer = new StringBuilder();
            int ch;
            while ((ch = bufferedReader.read()) != -1) {
                // 将字符添加到缓冲区
                buffer.append((char) ch);
                // 检查缓冲区是否包含完整的中文字符
                if (buffer.toString().endsWith("data: ")) {
                    String sub = buffer.substring(0, buffer.length() - 6);
                    if (sub.contains("[DONE]")) {
                        buffer.setLength(0);
                        break;
                    }
                    if (sub.length() > 0) {
                        try {
                            log.debug(sub);
                            ChatCompletion chatCompletion = JSONUtil.toBean(sub, ChatCompletion.class);
                            String s = chatCompletion.getChoices().get(0).getDelta().getContent();
                            if (s != null && s.length() > 0) {
                                log.info(s);
                                sseEmitter.send(SseEmitter.event().data(s));
                            }
                        } catch (Exception e) {
                            log.error(sub, e);
                        }
                    }
                    buffer.setLength(0);
                }
            }
            bufferedReader.close();
            inputStreamReader.close();
            return null;
        };

        // 发送请求
        restTemplate.execute(ChatConstant.CHAT_GPT_URL, HttpMethod.POST, requestCallback, responseExtractor);
    }

    public void chatNoStream(AskReq askReq, SseEmitter sseEmitter) {
        SseEmitterCache.setContent(askReq.getUuid(), Model.Message.ROLE_USER, askReq.getQuestion());
        Model model = new Model(SseEmitterCache.getContent(askReq.getUuid()));

        String result = HttpRequest.post(ChatConstant.CHAT_GPT_URL)
                .header(Header.AUTHORIZATION.getValue(), "Bearer " + ChatConstant.CHAT_GPT_KEY)
                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .setHttpProxy(ProxyConstant.PROXY_IP, ProxyConstant.PROXY_PORT)
                .body(JSONUtil.toJsonStr(model))
                .timeout(100000)
                .execute().body();
        log.debug(result);
        try {
            ChatCompletion chatCompletion = JSONUtil.toBean(result, ChatCompletion.class);
            ChatCompletion.Message message = chatCompletion.getChoices().get(0).getMessage();
            SseEmitterCache.setContent(askReq.getUuid(), message.getRole(), message.getContent());
            String content = message.getContent();
            if (content != null) {
                log.info(content);
                content = content.replace("\n", "<br>");
                sseEmitter.send(SseEmitter.event().data(content));
            }
        } catch (ConvertException e) {
            log.error("json转换失败：{}", result, e);
        } catch (Exception e) {
            log.error("SSE 异常", e);
        }
    }

}
