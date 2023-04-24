package com.alageek.chatgpt.dto;

import lombok.Data;

@Data
public class AskReq {
    private String uuid;
    private String key;
    private String question;
}
