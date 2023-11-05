package com.fm.api.controller;

import com.fm.api.error.ErrorMessage;
import com.fm.api.payload.response.ResponseObject;
import com.fm.api.representation.UpdateUserRequest;
import com.fm.api.service.UserDetail;
import com.fm.api.service.UserService;
import com.fm.base.models.dto.UserDTO;
import com.fm.base.models.dto.UsersRole;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping({"/user", "/users"})
@AllArgsConstructor
class UserController {
    private UserService userService;

    @GetMapping({"/", "all"})
    public ResponseEntity<?> getAllUser() {
        return ResponseObject.success(userService.getAllUser());
    }

    @GetMapping({"/pagination"})
    public ResponseEntity<?> getUserPagination(@RequestParam(value = "page", defaultValue = "1") int page,
                                               @RequestParam(value = "size", defaultValue = "20") int size,
                                               @RequestParam(value = "orderBy", defaultValue = "createdAt") String orderBy,
                                               @RequestParam(value = "desc", defaultValue = "false") boolean desc) {
        return ResponseObject.success(userService.listUserPagination(page, size, orderBy, desc));
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<?> getById(@Valid @PathVariable("id") Integer id) {
        return userService.getById(id).map(user -> ResponseObject.success(userService.getById(id)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping({"/createUser"})
    public ResponseEntity<?> create(@Valid @RequestBody UserDTO userDTO) {
        return ResponseObject.createSuccess(userService.create(userDTO.mapToUser().trimSpace()).mapToUserDTO());
    }

    @GetMapping({"/filter"})
    public ResponseEntity<?> filter(
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "orderBy", defaultValue = "createAt") String orderBy,
            @RequestParam(value = "desc", defaultValue = "false") boolean desc) {
        return ResponseObject.success(userService.filterUser(userName, email, name, phoneNumber, role, page, size, orderBy, desc));
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<?> delete(@Valid @PathVariable("id") Integer id) {
        userService.delete(id);
        return ResponseObject.success("Delete Successfully");
    }

    @GetMapping({"/profile"})
    public ResponseEntity<?> profile() {
        Integer id = UserDetail.getAuthorizedUser().getId();
        return ResponseObject.success(userService.getById(id));
    }

    @PatchMapping({"/update"})
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest userRequest) {
        return ResponseObject.success(userService.updateUser(userRequest.trimSpace()));
    }

    @PatchMapping({"/updateUser/{id}"})
    public ResponseEntity<?> adminUpdateUser(@PathVariable("id") Integer id,
                                             @RequestBody UpdateUserRequest userRequest) {
        return ResponseObject.success(userService.adminUpdateUser(id, userRequest.trimSpace()));
    }

    @PutMapping({"/update-users-role"})
    public ResponseEntity<?> updateUsersRole(@Valid @RequestBody UsersRole usersRole) {
        return ResponseObject.success(userService.updateUsersRole(usersRole.getUserIds(), usersRole.getRole()));
    }

    @PutMapping({"/change-password"})
    public ResponseEntity<?> changePassword(
            @RequestParam(value = "oldPassword") String oldPassword,
            @RequestParam(value = "newPassword") String newPassword
    ) {
        try {
            String currentPassword = UserDetail.getAuthorizedUser().getPassword();
            if (BCrypt.checkpw(oldPassword, currentPassword)) {
                return ResponseObject.success(userService.changePassword(UserDetail.getAuthorizedUser().getId(), newPassword));
            } else return ResponseObject.build(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_OLD_PASSWORD, null);
        } catch (Exception e) {
            return ResponseObject.build(HttpStatus.BAD_REQUEST, ErrorMessage.FAILED_TO_CHANGE_PASSWORD, null);
        }
    }

    @SneakyThrows
    @PostMapping({"/reset-password"})
    public ResponseEntity<?> resetPassword(@RequestParam(value = "userNameOrEmail") String userNameOrEmail,
                                           @RequestParam(value = "newPassword") String newPassword,
                                           @RequestParam(value = "OTP") String OTP) {
        return ResponseObject.success(userService.resetPassword(userNameOrEmail, newPassword, OTP));
    }

    @GetMapping({"/partners"})
    public ResponseEntity<?> listPartner() {
        return ResponseObject.success(userService.listPartner());
    }

}