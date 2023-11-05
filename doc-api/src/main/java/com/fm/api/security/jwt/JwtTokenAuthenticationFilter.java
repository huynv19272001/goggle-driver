package com.fm.api.security.jwt;

import com.fm.api.service.UserService;
import com.fm.base.models.sql.BlacklistToken;
import com.fm.base.repository.sql.BlacklistTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    UserService userService;
    @Autowired
    BlacklistTokenRepository blacklistTokenRepository;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenAuthenticationFilter.class);


    @Override
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("authorization");
        try {
            String jwt = parseJwt(request);
            if(authHeader != null){
                String token = authHeader.substring(7);
                BlacklistToken blacklistToken = blacklistTokenRepository.findByAccessToken(token);
                if(blacklistToken != null) {
                    jwt = null;
                }
            }

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String authorization = request.getParameter("Authorization") != null ? request.getParameter("Authorization") : request.getHeader("Authorization");

        if (authorization != null) {
            authorization = URLDecoder.decode(authorization, StandardCharsets.UTF_8);
        }

        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7, authorization.length());
        }

        return null;
    }
}