package dr.dev.scoretuneapi.project.model.dto;

import dr.dev.scoretuneapi.project.model.ProjectCategory;
import dr.dev.scoretuneapi.project.model.ProjectType;
import dr.dev.scoretuneapi.project.model.ProjectZone;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProjectRequestDto(
        @NotBlank(message = "Name is required")
        String name,
        @NotNull(message = "Release date is required")
        LocalDate releaseDate,
        @NotNull(message = "Type is required")
        ProjectType type,
        @NotNull(message = "Category is required")
        ProjectCategory category,
        @NotNull(message = "Zone is required")
        ProjectZone zone,
        String coverLink,
        List<UUID> artistIds,
        @NotEmpty(message = "At least one track is required")
        @Valid
        List<TrackRequestDto> tracks
) {
    public ProjectRequestDto {
        artistIds = artistIds == null ? List.of() : artistIds;
    }
}
