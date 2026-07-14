package dr.dev.scoretuneapi.spotify.service;

import dr.dev.scoretuneapi.core.exception.SpotifyException;
import dr.dev.scoretuneapi.spotify.connector.SpotifyConnector;
import dr.dev.scoretuneapi.spotify.model.dto.SpotifyArtistPhotoDto;
import dr.dev.scoretuneapi.spotify.model.dto.SpotifyProjectCoverDto;
import dr.dev.scoretuneapi.spotify.model.dto.SpotifyProjectTrackDto;
import dr.dev.scoretuneapi.spotify.model.dto.SpotifyProjectTracklistDto;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public SpotifyProjectCoverDto getProjectCover(String name, List<String> artists) {
        if (name == null || name.isBlank()) {
            throw new SpotifyException(SpotifyException.Code.NAME_REQUIRED, null);
        }

        String coverUrl = spotifyConnector.findProjectCoverUrl(name.trim(), artists);
        return new SpotifyProjectCoverDto(coverUrl);
    }

    @Override
    public SpotifyProjectTracklistDto getProjectTracklist(String name, List<String> artists) {
        if (name == null || name.isBlank()) {
            throw new SpotifyException(SpotifyException.Code.NAME_REQUIRED, null);
        }

        List<String> trackNames = spotifyConnector.findProjectTracklist(name.trim(), artists);
        List<SpotifyProjectTrackDto> tracks = trackNames.stream()
                .map(SpotifyProjectTrackDto::new)
                .toList();

        return new SpotifyProjectTracklistDto(tracks);
    }
}
