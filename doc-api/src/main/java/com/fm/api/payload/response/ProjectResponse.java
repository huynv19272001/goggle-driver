package com.fm.api.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@With
@AllArgsConstructor
@Data
public class ProjectResponse {

    private Integer id;

    private String name;

    private String code;

    private Long numberOrder;

    private String dashboard;

    private UserResponse user;
}
