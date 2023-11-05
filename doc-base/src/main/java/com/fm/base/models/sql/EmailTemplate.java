package com.fm.base.models.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "email_templates")
public class EmailTemplate extends BaseModel {
//    public static final String DEFAULT_SERVICE = "smtp";

    @Column(name = "key")
    private String key;

    @Column(name = "sender")
    private String sender;

    @Column(name = "subject")
    private String subject;

    @Column(name = "body")
    private String body;

    @Column(name = "htmlEnabled", columnDefinition = "boolean default true")
    private Boolean htmlEnabled = Boolean.TRUE;

//    @Column(name = "service", columnDefinition = DEFAULT_SERVICE)
//    private String service = DEFAULT_SERVICE;

    public static class Key {
        public static final String LEAVE_OF_ABSENCE = "LEAVE_OF_ABSENCE";
        public static final String PASSWORD_RESET = "PASSWORD_RESET";
    }
}
