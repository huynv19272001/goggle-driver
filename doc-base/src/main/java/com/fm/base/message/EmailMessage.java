package com.fm.base.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fm.base.models.sql.BaseModel;
import com.fm.base.models.sql.EmailTemplate;
import lombok.*;

import javax.persistence.Entity;
import java.util.List;
import java.util.Map;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailMessage  {
    public static final String DEFAULT_SERVICE = "sendgrid";
    public static final String DEFAULT_SERVICE_SEND_MAIL = "smtp";

    @JsonProperty("sender")
    public String sender;

    @JsonProperty("subject")
    public String subject;

    @JsonProperty("recipients")
    public List<String> recipients;

    @JsonProperty("body")
    public String body;

    @JsonProperty("attachmentsInBase64")
    public Map<String, String> attachmentsInBase64;

    @JsonProperty("imagesInBase64")
    public Map<String, String> imagesInBase64;

    @JsonProperty("ccs")
    public List<String> ccs;

    @JsonProperty("bccs")
    public List<String> bccs;

    @JsonProperty("htmlEnabled")
    public Boolean htmlEnabled = Boolean.TRUE;

    @JsonProperty("service")
    public String service = DEFAULT_SERVICE;

    public static EmailMessage fromTemplate(final EmailTemplate emailTemplate) {
        final EmailMessage emailMessage = new EmailMessage();
        emailMessage.sender = emailTemplate.getSender();
        emailMessage.subject = emailTemplate.getSubject();
        emailMessage.body = emailTemplate.getBody();
        emailMessage.htmlEnabled = emailTemplate.getHtmlEnabled();
//        emailMessage.service = emailTemplate.getService();
        emailMessage.service = DEFAULT_SERVICE_SEND_MAIL;
        return emailMessage;
    }
}
