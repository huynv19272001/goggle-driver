package com.fm.base.models.enums;

public enum MediaType {
    USER("user/"),
    FILE("upload/");
    private String custom;
    private MediaType(String custom) {
        this.custom = custom;
    }
    public String getMediaType() {
        return custom;
    }


}
