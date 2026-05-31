package dr.dev.scoretuneapi.project.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record TrackRequestDto(
        @NotNull(message = "Track number is required")
        @Min(value = 1, message = "Track number must be at least 1")
        Integer trackNumber,
        @NotBlank(message = "Track name is required")
        String name,
        @NotEmpty(message = "At least one interpreter is required")
        List<UUID> interpreterIds,
        List<UUID> featuringIds
) {
    public TrackRequestDto {
        featuringIds = featuringIds == null ? List.of() : featuringIds;
    }
}
