package dr.dev.scoretuneapi.spotify.connector.dto;

import java.util.List;

public record SpotifyAlbumTracksResponse(
        List<TrackItem> items
) {
    public record TrackItem(
            String name
    ) {
    }
}
