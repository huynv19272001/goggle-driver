package com.fm.base.message.core;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class Exchange {
    private String name;
    public Exchange withName(String name) {
        this.name = name;
        return this;
    }
}
