package com.crm.system.utils.mockAnnotations;

import com.crm.system.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithSecurityContextFactory;


public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    public WithMockCustomUserSecurityContextFactory(UserRepository userRepository, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser withMockCustomUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        System.out.println("1");
        UserDetails principal = userDetailsService.loadUserByUsername(withMockCustomUser.username());
        System.out.println("2");
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal,
                principal.getPassword(),
                principal.getAuthorities());
        context.setAuthentication(authentication);
        return context;


//        Collection<SimpleGrantedAuthority> authorityList = new ArrayList<>();
//
//        for (String role : withMockCustomUser.roles()) {
//            authorityList.add(new SimpleGrantedAuthority(role))    ;
//        }
//
//        UserDetails userDetails = new User(withMockCustomUser.username(), withMockCustomUser.password(), authorityList);
//        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(withMockCustomUser.id(),
//                withMockCustomUser.username(),
//                withMockCustomUser.email(),
//                withMockCustomUser.password(),
//                authorityList);
//
//        context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetailsImpl,
//                withMockCustomUser.password(),
//                userDetails.getAuthorities()));
//        return context;
    }
}
