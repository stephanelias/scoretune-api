package dr.dev.scoretuneapi.spotify.service;

import dr.dev.scoretuneapi.core.exception.SpotifyException;
import dr.dev.scoretuneapi.spotify.connector.SpotifyConnector;
import dr.dev.scoretuneapi.spotify.model.dto.SpotifyArtistPhotoDto;
import org.springframework.stereotype.Service;

@Service
public class SpotifyServiceImpl implements SpotifyService {

    private final SpotifyConnector spotifyConnector;

    public SpotifyServiceImpl(SpotifyConnector spotifyConnector) {
        this.spotifyConnector = spotifyConnector;
    }

    @Override
    public SpotifyArtistPhotoDto getArtistPhoto(String name) {
        if (name == null || name.isBlank()) {
            throw new SpotifyException(SpotifyException.Code.NAME_REQUIRED, null);
        }

        String photoUrl = spotifyConnector.findArtistPhotoUrl(name.trim());
        return new SpotifyArtistPhotoDto(photoUrl);
    }
}
