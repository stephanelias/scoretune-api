package dr.dev.scoretuneapi.core.presentation;

import dr.dev.scoretuneapi.core.dto.LoginResponseDto;
import dr.dev.scoretuneapi.core.dto.LoginUserDto;
import dr.dev.scoretuneapi.core.dto.RegisterUserDto;
import dr.dev.scoretuneapi.core.service.AuthService;
import dr.dev.scoretuneapi.core.service.JwtService;
import dr.dev.scoretuneapi.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
public class AuthApi {

    private final JwtService jwtService ;
    private final AuthService authService ;

    @Autowired
    public AuthApi(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authService.register(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authService.login(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponseDto loginResponse = new LoginResponseDto(jwtToken,jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
}
