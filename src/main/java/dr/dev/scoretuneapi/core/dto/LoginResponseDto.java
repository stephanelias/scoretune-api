package dr.dev.scoretuneapi.core.dto;

public record LoginResponseDto(String token,
                               long expiresIn) {
}
