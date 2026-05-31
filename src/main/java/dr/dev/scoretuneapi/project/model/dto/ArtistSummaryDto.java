package dr.dev.scoretuneapi.project.model.dto;

import dr.dev.scoretuneapi.artist.model.ArtistType;

import java.util.UUID;

public record ArtistSummaryDto(
        UUID id,
        String name,
        ArtistType type,
        String photoLink
) {
}
