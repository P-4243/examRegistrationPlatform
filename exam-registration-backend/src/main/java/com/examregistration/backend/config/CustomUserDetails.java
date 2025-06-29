package com.examregistration.backend.config;

import com.examregistration.backend.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private String role; // Our custom field for role
    private Long userId; // <--- ADD THIS FIELD

    public CustomUserDetails(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.userId = user.getId(); // <--- INITIALIZE THIS FIELD FROM YOUR USER ENTITY
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Map our role string to Spring Security's GrantedAuthority
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    // <--- ADD THIS GETTER METHOD
    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}