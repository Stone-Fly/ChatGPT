package com.alageek.chatgpt.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatCompletion {
    private String id;
    private String object;
    private long created;
    private String model;
    private Usage usage;
    private List<Choice> choices;

    @Data
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }

    @Data
    public static class Choice {
        private Message message;
        private String finish_reason;
        private int index;
        private Message delta;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}
