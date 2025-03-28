package com.prolink.messaging.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    private String sender;
    private String receiver;
    private String content;
    public MessageRequest(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }
}
