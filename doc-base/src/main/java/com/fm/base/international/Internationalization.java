/*
package com.fm.base.international;

import com.fm.base.configuration.DictConfiguration;
import com.fm.base.configuration.DictConfiguration.Message;
import com.fm.base.utils.DocfmResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;

public class Internationalization {
    private final DictConfiguration buildException;

    public Internationalization(DictConfiguration buildException) {
        this.buildException = buildException;
    }
    public static class Language {
        public static final String EN = "en";
        public static final String MY = "my";
    }

    public static class Key {

        public static final String USER_NOT_FOUND = "userNotFound";

    }
    public ClientErrorException exception(String key, Response.Status status) {
        Pair<String, String> message = findMessage(key);
        return DocfmResponse.buildException(status, message.getLeft(), message.getRight());
    }
    private Pair<String, String> findMessage(String key) {
        String msg = "";
        String msgMy = "";
        for (Field field : buildException.getClass().getDeclaredFields()) {
            try {
                Message message = (Message) field.get(buildException);
                for (Field messageField : message.getClass().getDeclaredFields()) {
                    if (messageField.getName().equals(key)) {
                        if (field.getName().equals(Language.EN)) {
                            msg = (String) messageField.get(message);
                        } else if (field.getName().equals(Language.MY)) {
                            msgMy = (String) messageField.get(message);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
//                e.printStackTrace();
            }
        }
        return Pair.of(msg, msgMy);
    }
}
*/
