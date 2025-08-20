package dr.dev.scoretuneapi.core.utils;

import dr.dev.scoretuneapi.user.model.Role;
import dr.dev.scoretuneapi.user.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Set;
import java.util.UUID;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFullName(annotation.fullName());
        user.setEmail(annotation.username());
        user.setPassword("password");
        user.setRoles(Set.of(Role.valueOf(annotation.roles()[0])));

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, "password", user.getAuthorities());
        context.setAuthentication(auth);

        return context;
    }
}