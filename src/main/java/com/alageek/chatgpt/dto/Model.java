package com.alageek.chatgpt.dto;

import lombok.Data;

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
    public static class Message {
        private String role = "user";
        private String content;

        public Message(String content) {
            this.content = content;
        }
    }

}
