package com.fm.api.service;

import com.fm.api.error.ErrorMessage;
import com.fm.api.representation.UpdateUserRequest;
import com.fm.api.utils.ListResult;
import com.fm.base.managers.Cache;
import com.fm.base.models.sql.User;
import com.fm.base.repository.sql.UserRepository;
import com.fm.base.utils.PageableUtils;
import com.fm.base.validation.ValidateInputUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import static com.fm.base.utils.PageableUtils.pageable;


@Service
@Slf4j
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userDAO;

    public ListResult<User> listUserPagination(int page, int size, String orderBy, boolean desc){
        return ListResult.from(userDAO.findAll(pageable(page,size,orderBy,desc)));
    }

    public List<User> getAllUser() {
        return userDAO.findAll();
    }

    public User create(User user) {
        userDAO.findByUserNameOrEmailOrPhoneNumber(user.getUserName(), user.getEmail(), user.getPhoneNumber()).stream().findFirst().ifPresent(existsUser -> {
            if (existsUser.getUserName().equals(user.getUserName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.EXISTS_USER_NAME);
            }
            if (existsUser.getEmail().equals(user.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.EXISTS_EMAIL);
            }
            if (existsUser.getPhoneNumber().equals(user.getPhoneNumber())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.EXISTS_PHONE_NUMBER);
            }
        });
        user.setPassword(new BCryptPasswordEncoder(4).encode(user.getPassword()));
        return userDAO.save(user);
    }

    public Optional<User> getById(Integer id) {
        return userDAO.findById(id);
    }

    public void delete(Integer userId) {
        userDAO.findById(userId).ifPresentOrElse(user -> userDAO.deleteById(userId)
                , () -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.USER_ID_NOT_EXISTS);
                });
    }

    public ListResult<User>filterUser(String userName, String email,String name, String phoneNumber,String role, int page, int size, String orderBy, boolean desc) {
        if(StringUtils.isNotBlank(userName)){
            userName = userName.replaceAll("\\s+", " ").trim();
        }
        if(StringUtils.isNotBlank(email)){
            email = email.replaceAll("\\s+", " ").trim();
        }
        if(StringUtils.isNotBlank(name)){
            name = name.replaceAll("\\s+", " ").trim();
        }
        if(StringUtils.isNotBlank(phoneNumber)){
            phoneNumber = phoneNumber.replaceAll("\\s+", " ").trim();
        }
        return ListResult.from(userDAO.filterUser(userName, email,name, phoneNumber,role, PageableUtils.pageable(page, size, orderBy, desc)));
    }

    public User updateUser(UpdateUserRequest userRequest) {
        Integer userIdLogin = UserDetail.getAuthorizedUser().getId();
        User user = userDAO.getById(userIdLogin);
        if (userRequest.getName() != null) {
            user.setName(userRequest.getName());
        }
        if (userRequest.getEmail() != null) {
            userDAO.findUserByEmail(userRequest.getEmail()).ifPresent(existsUser -> {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.EXISTS_EMAIL);
            });
            user.setEmail(userRequest.getEmail());
        }
        if (userRequest.getPhoneNumber() != null) {
            userDAO.findUserByPhoneNumber(userRequest.getPhoneNumber()).ifPresent(existsUser -> {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.EXISTS_PHONE_NUMBER);
            });
            user.setPhoneNumber(userRequest.getPhoneNumber());
        }
        return userDAO.save(user);
    }


    public User adminUpdateUser(Integer id, UpdateUserRequest userRequest) {
        Optional<User> optionalUser = userDAO.findById(id);
        optionalUser.ifPresentOrElse(user -> {
            if (userRequest.getName() != null) {
                user.setName(userRequest.getName());
            }
            if (userRequest.getEmail() != null) {
                userDAO.findUserByEmail(userRequest.getEmail()).ifPresent(existsUser -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.EXISTS_EMAIL);
                });
                user.setEmail(userRequest.getEmail());
            }
            if (userRequest.getPhoneNumber() != null) {
                userDAO.findUserByPhoneNumber(userRequest.getPhoneNumber()).ifPresent(existsUser -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.EXISTS_PHONE_NUMBER);
                });
                user.setPhoneNumber(userRequest.getPhoneNumber());
            }
            if (userRequest.getRole() != null) {
                user.setRole(userRequest.getRole());
            }
            userDAO.save(user);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.USER_ID_NOT_EXISTS);
        });
        return userDAO.getById(id);
    }

    public List<User> updateUsersRole(List<Integer> userIds, User.Role role) {
        List<User> listUserByIds = userDAO.findUserByIdIn(userIds);
        return userDAO.saveAll(listUserByIds.stream().peek(user -> user.setRole(role)).collect(Collectors.toList()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetail.build(user);
    }

    public User changePassword(Integer id, String newPassword) {
        User userOld = userDAO.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage.USER_ID_NOT_EXISTS));
        userOld.setPassword(new BCryptPasswordEncoder(4).encode(newPassword));
        return userDAO.save(userOld);
    }

    public List<User> listPartner() {
        return userDAO.listPartner();
    }

//    public String demandResetPassword(String userNameOrEmail) throws ExecutionException {
//        User user = userDAO.findByUserNameOrEmail(userNameOrEmail).orElseThrow(() -> new ResponseStatusException(HttpStatus.EXPECTATION_FAILED));
////        String otp = generateOTP();
//        String otp = Cache.resetTokenCache.get(user.getId()).getToken();
//
//        emailService.sendWithTemplateKey(EmailTemplate.Key.PASSWORD_RESET, Collections.singletonList(user.getEmail()), Map.of("otp", otp));
//        return otp;
//    }
//
//    public String generateOTP() {
//        return new DecimalFormat("000000").format(new Random().nextInt(999999));
//    }

    public String resetPassword(String userNameOrEmail, String newPassword, String OTP) throws ExecutionException {
        User user = userDAO.findByUserNameOrEmail(userNameOrEmail).orElseThrow(() -> new ResponseStatusException(HttpStatus.EXPECTATION_FAILED));
        if (Cache.resetTokenCache.get(user.getId()).getToken().equals(OTP)) {
            if (!ValidateInputUser.isValidPassword(newPassword)) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
            } else {
                user.setPassword(new BCryptPasswordEncoder(4).encode(newPassword));
                userDAO.save(user);
                return "Reset password Successfully";
            }
        } else return "OTP invalid";
    }

}
