package com.alten.shop.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class AdminUserPrincipal implements UserDetails, java.security.Principal {
    private static final long serialVersionUID = 1L;

    public AdminUserPrincipal() {
    }

    @Override
    public String getName() {
        return "admin@admin.com";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return "admin123";
    }

    @Override
    public String getUsername() {
        return "admin@admin.com";
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

    @Override
    public String toString() {
        return "AdminUserPrincipal(admin@admin.com)";
    }
}
