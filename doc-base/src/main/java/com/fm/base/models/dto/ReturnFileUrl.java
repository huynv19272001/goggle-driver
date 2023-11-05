package com.fm.base.models.dto;

import com.fm.base.models.sql.FileAttachment;
import com.fm.base.models.sql.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnFileUrl implements Serializable {
    private Order order;

    private List<saveFileOrder> fileNewPush;

    private List<FileAttachment> fileAttachments;

    @NoArgsConstructor
    @Data
    public static class saveFileOrder implements Serializable {
        private Integer id;

        private String fileName;

        private String fileUrl;

        private String toWatchFile;

        private Integer totalPage;

        public saveFileOrder(Integer id,String fileName, String filePath,String toWatchFile,int numberOfPages) {
            this.id = id;
            this.fileName = fileName;
            this.fileUrl = filePath;
            this.toWatchFile = toWatchFile;
            this.totalPage = numberOfPages;
        }

    }

}
