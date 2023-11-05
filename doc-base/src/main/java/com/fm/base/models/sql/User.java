package com.fm.base.models.sql;

import com.fm.base.message.ErrorMessage;
import com.fm.base.models.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseModel {

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "email")
    private String email;

    public enum Role {
        ADMIN, MANAGER, STAFF, USER
    }

    public User trimSpace() {
        if (userName != null && password != null && phoneNumber != null && email != null) {
            userName = userName.replaceAll("\\s+", " ").trim();
            name = name.replaceAll("\\s+", " ").trim();
            password = password.replaceAll("\\s+", " ").trim();
            email = email.replaceAll("\\s+", " ").trim();
            phoneNumber = phoneNumber.replaceAll("\\s+", " ").trim();
        }
        return this;
    }

    public UserDTO mapToUserDTO() {
        return new UserDTO().withId(this.getId()).
                withCreatedAt(this.getCreatedAt())
                .withUpdatedAt(this.getUpdatedAt())
                .withDeletedAt(this.getUpdatedAt())
                .withUserName(userName)
                .withPassword(password)
                .withEmail(email)
                .withName(name)
                .withPhoneNumber(phoneNumber)
                .withRole(role);
    }
}
