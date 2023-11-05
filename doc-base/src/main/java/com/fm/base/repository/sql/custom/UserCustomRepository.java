package com.fm.base.repository.sql.custom;

import com.fm.base.models.sql.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserCustomRepository {

    Page<User> filterUser(String userName, String email ,String name, String phoneNumber, String role, Pageable pageable);
}
