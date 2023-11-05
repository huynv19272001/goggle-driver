package com.fm.api.payload.response;

import lombok.Data;

@Data
public class FileDBResponse  {
    private Integer id;
    private String name;
    private String url;
    private String type;


    public FileDBResponse(Integer id,String name,String url,String type) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.type = type;
    }



}

