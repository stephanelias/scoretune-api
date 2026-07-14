package dr.dev.scoretuneapi.spotify.service;

import dr.dev.scoretuneapi.spotify.model.dto.SpotifyArtistPhotoDto;
import dr.dev.scoretuneapi.spotify.model.dto.SpotifyProjectCoverDto;

import java.util.List;

public interface SpotifyService {

    SpotifyArtistPhotoDto getArtistPhoto(String name);

    SpotifyProjectCoverDto getProjectCover(String name, List<String> artists);
}
