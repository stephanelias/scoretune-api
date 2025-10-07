package dr.dev.scoretuneapi.core.service;

import dr.dev.scoretuneapi.core.dto.LoginUserDto;
import dr.dev.scoretuneapi.core.dto.RegisterUserDto;
import dr.dev.scoretuneapi.core.exception.UserException;
import dr.dev.scoretuneapi.user.model.Role;
import dr.dev.scoretuneapi.user.model.User;
import dr.dev.scoretuneapi.user.persistence.UserDao;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    private final UserDao userDao ;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserDao userDao,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public User register(RegisterUserDto registerUserDto) {
        if (userDao.findByEmail(registerUserDto.email()).isPresent())
            throw new UserException(UserException.Code.ALREADY_EXISTS,null,"A user with this email already exists");
        User user = new User.Builder()
                .withEmail(registerUserDto.email())
                .withPassword(passwordEncoder.encode(registerUserDto.password()))
                .withFullName(registerUserDto.fullName())
                .withRoles(Set.of(Role.ROLE_USER))
                .build();
        return userDao.save(user) ;
    }

    public User login(LoginUserDto loginUserDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.email(),
                        loginUserDto.password()
                )
        );
        return userDao.findByEmail(loginUserDto.email())
                .orElseThrow();
    }
}
