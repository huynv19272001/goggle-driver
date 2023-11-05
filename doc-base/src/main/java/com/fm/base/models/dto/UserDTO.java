package com.fm.base.models.dto;

import com.fm.base.message.ErrorMessage;
import com.fm.base.models.sql.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.hibernate.validator.constraints.Length;
import org.joda.time.DateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Integer id;

    private DateTime createdAt;

    private DateTime updatedAt;

    private DateTime deletedAt;

    @NotBlank(message = ErrorMessage.INVALID_USER_NAME)
    @Pattern(regexp = "[\\s a-zA-Z]+([_-](?![_-])|[a-zA-Z0-9\\s]){2,}$", message = ErrorMessage.INVALID_USER_NAME)
    @Length(max = 255, message = ErrorMessage.INVALID_USER_NAME)
    private String userName;

    @NotBlank(message = ErrorMessage.INVALID_PASSWORD)
    @Length(min = 6, max = 255, message = ErrorMessage.INVALID_PASSWORD)
    private String password;

    @Length(max = 255, message = ErrorMessage.INVALID_NAME)
    private String name;

    @NotBlank(message = ErrorMessage.INVALID_PHONE)
    @Pattern(regexp = "[\\s 0]+[0-9]{9}$", message = ErrorMessage.INVALID_PHONE)
    private String phoneNumber;

    @NotNull(message = ErrorMessage.ROlE_CAN_NOT_NULL)
    private User.Role role;

    @NotBlank(message = ErrorMessage.INVALID_EMAIL)
    @Pattern(regexp = "^[\\s a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9 \\s])?)*$", message = ErrorMessage.INVALID_EMAIL)
    private String email;

    public User mapToUser() {
        return  new User().withUserName(userName).withPassword(password).withEmail(email).withName(name).withPhoneNumber(phoneNumber).withRole(role);
    }
}
