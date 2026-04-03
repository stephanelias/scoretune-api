package dr.dev.scoretuneapi.artist.service;

import dr.dev.scoretuneapi.artist.model.dto.ArtistDto;

import java.util.List;
import java.util.UUID;

public interface ArtistService {

    List<ArtistDto> getAllArtists();

    ArtistDto getArtistById(UUID id);

    ArtistDto createArtist(ArtistDto artistDto);

    ArtistDto updateArtist(UUID id, ArtistDto artistDto);

    void deleteArtist(UUID id);
}
