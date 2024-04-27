package com.crm.system.utils.mockAnnotations;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithSecurityContextFactory;


public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUserByUsername> {
    private final UserDetailsService userDetailsService;

    public WithMockCustomUserSecurityContextFactory(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUserByUsername withMockCustomUserByUsername) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        UserDetails principal = userDetailsService.loadUserByUsername(withMockCustomUserByUsername.username());
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal,
                principal.getPassword(),
                principal.getAuthorities());
        context.setAuthentication(authentication);
        return context;
    }
}
