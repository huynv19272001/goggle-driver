package com.fm.base.message.core;

import com.fm.base.message.core.exceptions.MessageQueueIOException;

public interface MessageQueueProducer {
    void publish(Exchange exchange, String key, Object data) throws MessageQueueIOException;

    void publish(Exchange exchange, Object data) throws MessageQueueIOException;

    void schedule(Long ts, Exchange exchange, String key, Object data) throws MessageQueueIOException;

    void schedule(Long ts, Exchange exchange, Object data) throws MessageQueueIOException;

    void close();
}
