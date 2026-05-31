package dr.dev.scoretuneapi.project.model.dto;

import java.util.List;
import java.util.UUID;

public record ProjectAppearanceDto(
        UUID trackId,
        String trackName,
        int trackNumber,
        UUID projectId,
        String projectName,
        String projectCoverLink,
        List<ArtistSummaryDto> interpreters,
        List<ArtistSummaryDto> featurings
) {
}
