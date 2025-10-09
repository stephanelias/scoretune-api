package dr.dev.scoretuneapi.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginUserDto(@Email(message = "Invalid email")
                           @NotBlank(message = "Email is required")
                           String email,

                           @NotBlank(message = "Password is required")
                           @Size(min = 8, message = "Password must be at least 8 characters long")
                           String password) {
}
