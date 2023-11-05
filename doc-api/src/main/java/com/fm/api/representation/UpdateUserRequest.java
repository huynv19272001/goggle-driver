package com.fm.api.representation;

import com.fm.base.models.sql.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private User.Role role;

    public UpdateUserRequest trimSpace() {
        if(name !=null ) {
            name = name.replaceAll("\\s+", " ").trim();
        }
        if(email !=null ) {
            email = email.replaceAll("\\s+", " ").trim();
        }
        if(phoneNumber !=null ) {
            phoneNumber = phoneNumber.replaceAll("\\s+", " ").trim();
        }
        return this;
    }
}
