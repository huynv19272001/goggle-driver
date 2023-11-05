package com.fm.base.models.sql;

import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@With
@Entity
@Table(name = "file_attachment")
public class FileAttachment extends BaseModel {
    private String fileName;

    private String fileUrl;

    private String fileType;

    private Integer pageTotal;

    private Integer creatorId;

    private Integer orderId;

    public FileAttachment(String fileName, String fileUrl, String contentType, int numberOfPages, Integer id) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileType = contentType;
        this.pageTotal = numberOfPages;
        this.orderId = id;
    }

}
