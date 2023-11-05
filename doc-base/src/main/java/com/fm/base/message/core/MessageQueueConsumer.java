package com.fm.base.message.core;

import com.fm.base.message.core.exceptions.InvalidMessageFormatException;
import com.fm.base.message.core.exceptions.NeedRetryException;
import com.fm.base.message.core.exceptions.ShutdownCauseException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class MessageQueueConsumer<M> {
    protected List<Consumer<ListenerInput<M>>> listeners = new ArrayList<>();
    protected List<Consumer<ShutdownCauseException>> shutdownListeners = new ArrayList<>();
    ObjectMapper MAPPER;

    private ObjectMapper getMapper() {
        if (this.MAPPER != null) return MAPPER;

        return new ObjectMapper().registerModule(new JodaModule());
    }

    protected M parseMessage(String body, Class<M> classz) throws InvalidMessageFormatException, IOException {
        M message = null;

        try {
            if (classz.isInstance(body)) {
                message = classz.cast(body);
            } else {
                message = getMapper().readValue(body, classz);
            }
        } catch (JsonParseException e) {
            throw new InvalidMessageFormatException(e.getMessage());
        } catch (JsonMappingException e) {
            throw new InvalidMessageFormatException(e.getMessage());
        }

        return message;
    }


    public MessageQueueConsumer addListener(Consumer<ListenerInput<M>> listener) throws NeedRetryException {
        this.listeners.add(listener);
        if (isPausing()) resume();
        return this;
    }

    public MessageQueueConsumer addShutdownListener(Consumer<ShutdownCauseException> shutdownListener) {
        this.shutdownListeners.add(shutdownListener);

        return this;
    }

    abstract public void listenMessage(Consumer<QueueMessage> consumer) throws IOException;

    public void subscribe(Class<M> clazz) throws InvalidMessageFormatException, IOException {
        Consumer<QueueMessage> consumer = (queueMessage) -> {
            // process message
            M message = null;
            try {
                message = parseMessage(queueMessage.getMessage(), clazz);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (message == null) {
                throw new InvalidMessageFormatException("Message is invalid: ");
            }

            // get listeners for this topic
            if (listeners != null && listeners.size() > 0) {
                for (Consumer<ListenerInput<M>> listener: listeners) {
                    listener.accept(new ListenerInput(message, this));
                }
            } else {
                this.pause();
            }
        };

        this.listenMessage(consumer);
    }

    abstract public void pause();
    abstract public void resume();
    abstract public boolean isPausing();
}
