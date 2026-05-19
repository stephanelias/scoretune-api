package dr.dev.scoretuneapi.artist.service;

import dr.dev.scoretuneapi.artist.model.dto.ArtistDto;
import dr.dev.scoretuneapi.core.dto.PageResponse;

import java.util.UUID;

public interface ArtistService {

    PageResponse<ArtistDto> searchArtists(int page, int size, String search);

    ArtistDto getArtistById(UUID id);

    ArtistDto createArtist(ArtistDto artistDto);

    ArtistDto updateArtist(UUID id, ArtistDto artistDto);

    void deleteArtist(UUID id);
}
