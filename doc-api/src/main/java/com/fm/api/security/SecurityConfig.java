package com.fm.api.security;

import com.fm.api.security.jwt.AuthEntryPointJwt;
import com.fm.api.security.jwt.JwtTokenAuthenticationFilter;
import com.fm.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.fm.base.models.sql.User.Role.*;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;

    @Bean
    public JwtTokenAuthenticationFilter authenticationJwtTokenFilter() {
        return new JwtTokenAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthEntryPointJwt unauthorizedHandler() {
        return new AuthEntryPointJwt();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/auth/**").permitAll()
                .antMatchers(HttpMethod.GET, "/users/{id}", "/user/{id}").hasAnyAuthority(ADMIN.name())
                .antMatchers("/auth/login", "/auth/logout").permitAll()
                .antMatchers(HttpMethod.GET, "/users/all", "/user/all", "/users/", "/user/").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.PUT, "/users/update-users-role", "/user/update-users-role").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/users/{id}", "/user/{id}").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/users/filter", "/user/filter").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.PATCH, "/users/updateUser/{id}").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.POST, "/users/createUser").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/users/partners/","/user/partners/").hasAnyAuthority(ADMIN.name(), MANAGER.name())
                .antMatchers(HttpMethod.GET, "/users/pagination", "/users/pagination").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/users/search", "/user/search").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/excel/export/excel").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/users/profile", "/user/profile").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/price-list/search-project-id").hasAnyAuthority(ADMIN.name(), MANAGER.name())
                .antMatchers(HttpMethod.GET, "/price-list/search-project-id/pagination").hasAnyAuthority(ADMIN.name(), MANAGER.name())
                .antMatchers(HttpMethod.GET, "/price-list/create-price").hasAnyAuthority(ADMIN.name(), MANAGER.name())
                .antMatchers(HttpMethod.PUT, "/users/change-password", "/user/change-password").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name(), USER.name())
                .antMatchers(HttpMethod.POST, "/orders/create-order", "/order/create-order", "/order/create-order/", "/orders/create-order/").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name(), USER.name())
                .antMatchers(HttpMethod.POST, "/orders/edit-order", "/order/edit-order").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name(), USER.name())
                .antMatchers(HttpMethod.POST, "/orders/downloadOrderCSV", "/order/downloadOrderCSV").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name(), USER.name())
                .antMatchers(HttpMethod.GET, "/orders/list-order-by-user-login", "/order/list-order-by-user-login").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name(), USER.name())
                .antMatchers(HttpMethod.GET, "/orders/download-file", "/order/download-file").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name())
                .antMatchers(HttpMethod.PUT, "/orders/edit-note-order", "/order/edit-note-order").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name())
                .antMatchers(HttpMethod.PATCH, "/orders/update", "/order/update").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name())
                .antMatchers(HttpMethod.PATCH, "/orders/update-order", "/order/update-order").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name())
                .antMatchers(HttpMethod.POST, "/orders/{orderId}", "/orders").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name())
                .antMatchers(HttpMethod.POST, "/orders/update-status", "/update-status").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name())
                .antMatchers(HttpMethod.PUT, "/orders/edit-price-order", "/order/edit-price-order").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name())
                .antMatchers(HttpMethod.PATCH, "/orders/update-status", "/update-status").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name())
                .antMatchers(HttpMethod.POST, "/orders/edit-priceOrder", "/order/edit-priceOrder").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name())
                .antMatchers(HttpMethod.PUT, "/orders/update-status-orders", "/update-status-orders").hasAnyAuthority(ADMIN.name(), MANAGER.name())
                .antMatchers(HttpMethod.POST, "/orders/edit-priceOrder", "/order/edit-priceOrder").hasAnyAuthority(ADMIN.name(), STAFF.name())
                .antMatchers(HttpMethod.GET, "/orders/listOrderByUserLogin", "/order/listOrderByUserLogin").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name())
                .antMatchers(HttpMethod.POST, "/orders/updateOrder", "/order/updateOrder").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name())
                .antMatchers(HttpMethod.GET, "/orders/downloadFile", "/order/downloadFile").hasAnyAuthority(ADMIN.name(), MANAGER.name(), STAFF.name())
                .antMatchers(HttpMethod.POST, "/roles").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/orders/delete", "/order/delete").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.POST, "/approve/delete", "/approve/delete").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.POST, "/material/", "/material").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.PUT, "/material/{id}", "/material").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/material/", "/material").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/material/all").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/material/{id}").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.POST, "/namePublication/").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.PUT, "/namePublication/").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/namePublication/").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/namePublication/all").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/namePublication/").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/number-face/list").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.POST, "/number-face/").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.PUT, "/number-face/").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/number-face/").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/project", "/project/all", "/projects/all").hasAnyAuthority(ADMIN.name(), MANAGER.name(), USER.name(), STAFF.name())
                .antMatchers(HttpMethod.POST, "/project", "/project/", "/projects/").hasAnyAuthority(ADMIN.name(), MANAGER.name())
                .antMatchers(HttpMethod.PUT, "/project", "/project/", "/projects/").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/project", "/project/", "/projects/").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/type-paper/list").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/type-paper/").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.POST, "/type-paper/").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.PUT, "/type-paper/").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/type-paper/").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/report/**").hasAnyAuthority(ADMIN.name(), MANAGER.name())
                .antMatchers(HttpMethod.GET, "/excels/**").hasAnyAuthority(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/dashboard/**").hasAnyAuthority(ADMIN.name())
                .anyRequest().authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
