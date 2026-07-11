package dr.dev.scoretuneapi.spotify.connector.dto;

import java.util.List;

public record SpotifySearchArtistsResponse(
        Artists artists
) {
    public record Artists(
            List<ArtistItem> items
    ) {
    }

    public record ArtistItem(
            List<Image> images
    ) {
    }

    public record Image(
            String url,
            Integer height,
            Integer width
    ) {
    }
}
