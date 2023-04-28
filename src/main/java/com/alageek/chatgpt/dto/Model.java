package com.alageek.chatgpt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class Model {
    private String model = "gpt-3.5-turbo";
    private Double temperature = 0.7;
    private Boolean stream = false;
    private List<Message> messages;

    public Model(List<Message> messages) {
        this.messages = messages;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        public static final String ROLE_SYSTEM = "system";
        public static final String ROLE_USER = "user";
        public static final String ROLE_ASSISTANT = "assistant";

        private String role;
        private String content;
    }

}
