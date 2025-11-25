package com.alten.shop.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithAdminUserSecurityContextFactory implements WithSecurityContextFactory<WithAdminUser> {

    @Override
    public SecurityContext createSecurityContext(WithAdminUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        AdminUserPrincipal adminUserPrincipal = new AdminUserPrincipal();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                adminUserPrincipal,
                null,
                adminUserPrincipal.getAuthorities()
        );

        context.setAuthentication(auth);
        return context;
    }
}
