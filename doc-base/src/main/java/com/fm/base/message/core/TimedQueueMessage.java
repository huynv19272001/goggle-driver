package com.fm.base.message.core;

public class TimedQueueMessage {
    private String id; // for tracking purpose
    private String key;
    private String exchange;
    private String message;
    private Long ts;
    private Long received;
    private Long processed;

    public TimedQueueMessage() {
    }

    public TimedQueueMessage(String exchange, String key, String message, Long ts) {
        this.key = key;
        this.message = message;
        this.exchange = exchange;
        this.ts = ts;
    }

    public TimedQueueMessage withTs(Long ts) {
        this.ts = ts;
        return this;
    }

    public TimedQueueMessage withReceived(Long received) {
        this.received = received;
        return this;
    }

    public TimedQueueMessage withProcessed(Long processed) {
        this.processed = processed;
        return this;
    }

    public TimedQueueMessage withExchange(String exchange) {
        this.exchange = exchange;
        return this;
    }

    public TimedQueueMessage withId(String id) {
        this.id = id;
        return this;
    }

    public TimedQueueMessage withKey(String key) {
        this.key = key;
        return this;
    }

    public TimedQueueMessage withMessage(String message) {
        this.message = message;
        return this;
    }

    public String getKey() {
        return key;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getExchange() {
        return exchange;
    }

    public Long getTs() {
        return ts;
    }

    public Long getProcessed() {
        return processed;
    }

    public Long getReceived() {
        return received;
    }
}
