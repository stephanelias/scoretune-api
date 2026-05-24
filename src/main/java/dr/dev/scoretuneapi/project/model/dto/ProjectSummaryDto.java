package dr.dev.scoretuneapi.project.model.dto;

import dr.dev.scoretuneapi.project.model.ProjectCategory;
import dr.dev.scoretuneapi.project.model.ProjectType;
import dr.dev.scoretuneapi.project.model.ProjectZone;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectSummaryDto(
        UUID id,
        String name,
        LocalDate releaseDate,
        ProjectType type,
        ProjectCategory category,
        ProjectZone zone,
        String coverLink
) {
}
