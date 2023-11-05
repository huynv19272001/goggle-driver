package com.fm.base.message.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public interface MessageQueueBuilder {
    public MessageQueueProducer createProducer() throws IOException;
    public MessageQueueProducer createProducer(ObjectMapper MAPPER) throws IOException;
    public MessageQueueConsumer createConsumer(String queueName) throws IOException ;
    public void close() throws IOException ;
}
