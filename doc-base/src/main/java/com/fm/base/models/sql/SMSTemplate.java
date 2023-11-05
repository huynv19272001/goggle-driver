package com.fm.base.models.sql;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "sms_template")
@Data
@NoArgsConstructor
public class SMSTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String body;
    @Enumerated(EnumType.STRING)
    @Column(name = "key", unique = true)
    private Key key;
    private String subject;

    public enum Key {
        LEAVE_OF_ABSENCE, OTP, CUSTOM
    }
}
