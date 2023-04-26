package com.alageek.chatgpt.controller;

import cn.hutool.json.JSONUtil;
import com.alageek.chatgpt.cache.SseEmitterCache;
import com.alageek.chatgpt.constant.ChatConstant;
import com.alageek.chatgpt.constant.CommonConstant;
import com.alageek.chatgpt.dto.AskReq;
import com.alageek.chatgpt.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private ChatService chatService;

    @ResponseBody
    @RequestMapping("/getUUID")
    public String getUUid() {
        UUID uuid = UUID.randomUUID();
        while (SseEmitterCache.UUID_SSE_EMITTER.containsKey(uuid.toString())) {
            uuid = UUID.randomUUID();
        }
        return "{\"uuid\":\"" + uuid + "\"}";
    }

    @RequestMapping("/connect")
    public SseEmitter connect(String uuid) {
        if (StringUtils.isBlank(uuid) || "undefined".equals(uuid)) {
            return null;
        }
        SseEmitter emitter = new SseEmitter();
        SseEmitterCache.UUID_DATE.put(uuid, CommonConstant.yyyyMMdd.format(new Date()));
        SseEmitterCache.UUID_SSE_EMITTER.put(uuid, emitter);
        return emitter;
    }

    @PostMapping("/ask")
    public void ask(@RequestBody AskReq askReq) {
        log.info(JSONUtil.toJsonStr(askReq));
        if (StringUtils.isBlank(askReq.getUuid()) || StringUtils.isBlank(askReq.getQuestion())) {
            return;
        }
        SseEmitter sseEmitter = SseEmitterCache.UUID_SSE_EMITTER.get(askReq.getUuid());
        if (sseEmitter == null) {
            return;
        }
        if (StringUtils.isBlank(askReq.getKey())) {
            askReq.setKey(ChatConstant.CHAT_GPT_KEY);
        }
        chatService.chat(askReq, sseEmitter);
    }

    @PostMapping(value = "/askNoStream")
    public void askNoStream(@RequestBody AskReq askReq) {
        log.info(JSONUtil.toJsonStr(askReq));
        if (StringUtils.isBlank(askReq.getUuid()) || StringUtils.isBlank(askReq.getQuestion())) {
            return;
        }
        SseEmitter sseEmitter = SseEmitterCache.UUID_SSE_EMITTER.get(askReq.getUuid());
        if (sseEmitter == null) {
            return;
        }
        if (StringUtils.isBlank(askReq.getKey())) {
            askReq.setKey(ChatConstant.CHAT_GPT_KEY);
        }
        chatService.chatNoStream(askReq, sseEmitter);
    }

}
