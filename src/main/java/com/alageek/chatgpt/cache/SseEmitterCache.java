package com.alageek.chatgpt.cache;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterCache {

    public static final Map<String, SseEmitter> UUID_SSE_EMITTER = new ConcurrentHashMap<>();
    public static final Map<String, String> UUID_DATE = new ConcurrentHashMap<>();

}
