package dr.dev.scoretuneapi.project.model.dto;

import java.util.List;
import java.util.UUID;

public record TrackDto(
        UUID id,
        int trackNumber,
        String name,
        List<ArtistSummaryDto> interpreters,
        List<ArtistSummaryDto> featurings
) {
}
