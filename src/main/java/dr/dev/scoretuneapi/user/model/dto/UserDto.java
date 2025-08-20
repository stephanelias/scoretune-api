package dr.dev.scoretuneapi.user.model.dto;

import dr.dev.scoretuneapi.user.model.Role;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public record UserDto(UUID id,
                      String fullName,
                      String email,
                      Set<Role> roles,
                      Date createdAt) {
}
