package dr.dev.scoretuneapi.artist.model.dto;

import dr.dev.scoretuneapi.artist.model.ArtistType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ArtistDto(UUID id,
                        @NotBlank(message = "Name is required") String name,
                        @NotNull(message = "Type is required") ArtistType type,
                        String photoLink) {
}
