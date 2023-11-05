package com.fm.base.message.core;

public class ListenerInput<M> {
    public M message;
    public MessageQueueConsumer messageQueueConsumer;

    public ListenerInput(M message, MessageQueueConsumer messageQueueConsumer) {
        this.message = message;
        this.messageQueueConsumer = messageQueueConsumer;
    }
}
