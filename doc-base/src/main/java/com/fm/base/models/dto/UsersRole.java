package com.fm.base.models.dto;

import com.fm.base.models.sql.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersRole {
    private List<Integer> userIds;
    private User.Role role;
}
