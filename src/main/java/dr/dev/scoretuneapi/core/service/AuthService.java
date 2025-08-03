package dr.dev.scoretuneapi.core.service;

import dr.dev.scoretuneapi.core.dto.LoginUserDto;
import dr.dev.scoretuneapi.core.dto.RegisterUserDto;
import dr.dev.scoretuneapi.user.model.User;
import dr.dev.scoretuneapi.user.persistence.UserDao;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        User user = new User.Builder()
                .withEmail(registerUserDto.email())
                .withPassword(passwordEncoder.encode(registerUserDto.password()))
                .withFullName(registerUserDto.fullName())
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
