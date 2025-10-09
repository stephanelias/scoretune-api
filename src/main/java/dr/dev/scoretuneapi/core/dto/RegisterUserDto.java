package dr.dev.scoretuneapi.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserDto(@Email(message = "Invalid email")
                              @NotBlank(message = "Email is required")
                              String email,

                              @NotBlank(message = "Password is required")
                              @Size(min = 8, message = "Password must be at least 8 characters long")
                              String password,

                              @NotBlank(message = "Full name is required")
                              @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters")
                              String fullName) {

}
