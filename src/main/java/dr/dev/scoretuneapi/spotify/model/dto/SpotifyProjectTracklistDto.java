package dr.dev.scoretuneapi.spotify.model.dto;

import java.util.List;

public record SpotifyProjectTracklistDto(
        List<SpotifyProjectTrackDto> tracks
) {
}
