package com.fm.api.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@With
@AllArgsConstructor
@Data
public class UserResponse {

    private Integer id;

    private String userName;

    private String name;

    private String phoneNumber;

    private String email;
}
