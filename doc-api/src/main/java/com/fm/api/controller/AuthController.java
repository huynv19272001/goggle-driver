package com.fm.api.controller;


import com.fm.api.error.ErrorMessage;
import com.fm.api.payload.request.LogOutRequest;
import com.fm.api.payload.request.LoginRequest;
import com.fm.api.payload.request.TokenRefreshRequest;
import com.fm.api.payload.response.JwtResponse;
import com.fm.api.payload.response.ResponseObject;
import com.fm.api.payload.response.TokenRefreshResponse;
import com.fm.api.security.jwt.JwtUtils;
import com.fm.api.service.BlacklistService;
import com.fm.api.service.RefreshTokenService;
import com.fm.api.service.UserDetail;
import com.fm.api.service.UserService;
import com.fm.base.models.sql.BlacklistToken;
import com.fm.base.models.sql.RefreshToken;
import com.fm.base.models.sql.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @Autowired
    BlacklistService blacklistService;


    @Value("${security.jwt.expiration:}")
    private Long jwtExpirationMs;

    @Value("${security.jwt.jwtExpirationMs:}")
    private Long refreshTokenDurationMs;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword())
            );
            String notification = "Login successfully!!";

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetail userDetails = (UserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            String role = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining());

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            return ResponseObject.success(new JwtResponse(
                    jwt,
                    refreshToken.getToken(),
                    jwtExpirationMs,
                    refreshTokenDurationMs,
                    notification,
                    role
            ));
        } catch (BadCredentialsException e) {
            return ResponseObject.badRequest(ErrorMessage.INCORRECT_USERNAME_PASSWORD);
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    String token = jwtUtils.generateTokenFromUsername(userService.getById(userId).orElseThrow().getUserName());
                    return ResponseObject.success(new TokenRefreshResponse(token, requestRefreshToken, jwtExpirationMs, refreshTokenDurationMs));
                }).orElseGet(ResponseObject::badRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody LogOutRequest logOutRequest) {
        refreshTokenService.deleteByUserId(logOutRequest.getUserId());
        String authHeader =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
        BlacklistToken blacklistToken = new BlacklistToken();
        if(authHeader != null){
            blacklistToken.setAccessToken(authHeader.substring(7));
        }
        blacklistService.save(blacklistToken);
        return ResponseObject.success();
    }

    @GetMapping({"/whoami"})
    public ResponseEntity<?> findUserLogin() {
        try {
            return userService.getById(UserDetail.getAuthorizedUser().getId()).stream().map(ResponseObject::success).findAny()
                    .orElse(ResponseObject.notFound(ErrorMessage.USER_ID_NOT_EXISTS));
        } catch (Exception e) {
            return ResponseObject.badRequest();
        }
    }
}
