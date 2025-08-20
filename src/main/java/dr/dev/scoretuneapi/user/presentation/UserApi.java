package dr.dev.scoretuneapi.user.presentation;

import dr.dev.scoretuneapi.user.model.Role;
import dr.dev.scoretuneapi.user.model.User;
import dr.dev.scoretuneapi.user.model.dto.UserDto;
import dr.dev.scoretuneapi.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/api/users")
@RestController
public class UserApi {

    private final UserService userService;

    public UserApi(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> authenticatedUser(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(new UserDto(
                currentUser.getId(),
                currentUser.getFullName(),
                currentUser.getEmail(),
                currentUser.getRoles(),
                currentUser.getCreatedAt()
        ));
    }

    @GetMapping
    public ResponseEntity<List<User>> allUsers() {
        List<User> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }
}
