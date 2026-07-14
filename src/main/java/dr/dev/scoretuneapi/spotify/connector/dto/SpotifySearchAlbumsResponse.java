package dr.dev.scoretuneapi.spotify.connector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpotifySearchAlbumsResponse(
        Albums albums
) {
    public record Albums(
            List<AlbumItem> items
    ) {
    }

    public record AlbumItem(
            String id,
            @JsonProperty("album_type") String albumType,
            @JsonProperty("total_tracks") Integer totalTracks,
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
