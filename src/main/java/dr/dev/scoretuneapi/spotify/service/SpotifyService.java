package dr.dev.scoretuneapi.spotify.service;

import dr.dev.scoretuneapi.spotify.model.dto.SpotifyArtistPhotoDto;

public interface SpotifyService {

    SpotifyArtistPhotoDto getArtistPhoto(String name);
}
