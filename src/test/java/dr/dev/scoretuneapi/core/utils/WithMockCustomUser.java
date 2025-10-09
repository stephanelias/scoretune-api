package dr.dev.scoretuneapi.core.utils;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "user@test.com";
    String fullName() default "Test User";
    String[] roles() default {"ROLE_USER"};
}
