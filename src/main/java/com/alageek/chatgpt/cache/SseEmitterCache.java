package com.alageek.chatgpt.cache;

import com.alageek.chatgpt.constant.ChatConstant;
import com.alageek.chatgpt.dto.Model;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterCache {

    public static final Map<String, String> UUID_DATE = new ConcurrentHashMap<>();
    public static final Map<String, SseEmitter> UUID_SSE_EMITTER = new ConcurrentHashMap<>();
    public static final Map<String, List<Model.Message>> UUID_CONTENT = new ConcurrentHashMap<>();
    public static final Map<String, Integer> UUID_CONTENT_LENGTH = new ConcurrentHashMap<>();

    public static void setContent(String uuid, String role, String content) {
        List<Model.Message> list = UUID_CONTENT.getOrDefault(uuid, new ArrayList<>());
        Integer length = UUID_CONTENT_LENGTH.getOrDefault(uuid, 0);
        list.add(new Model.Message(role, content));
        length += content.length();
        while (length > ChatConstant.CHAT_CONTENT_MAX_LENGTH) {
            Model.Message message = list.get(0);
            list.remove(message);
            length -= message.getContent().length();
        }
        UUID_CONTENT.put(uuid, list);
        UUID_CONTENT_LENGTH.put(uuid, length);
    }

    public static List<Model.Message> getContent(String uuid) {
        return UUID_CONTENT.get(uuid);
    }

}
