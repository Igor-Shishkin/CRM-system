package com.crm.system.utils.mockAnnotations;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "user";
    String email() default "test@gmail.com";
    String password() default "password";
    String[] roles() default {"USER"};
    long id() default 1L;
}