package com.fm.base.message.core;

public class QueueMessage {
    private String key;
    private String message;

    public QueueMessage(String key, String message) {
        this.key = key;
        this.message = message;
    }

    public String getKey() {return key;}
    public String getMessage() {return message;}
}
