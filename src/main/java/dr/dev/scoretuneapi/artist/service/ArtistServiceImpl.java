package dr.dev.scoretuneapi.artist.service;

import dr.dev.scoretuneapi.artist.model.Artist;
import dr.dev.scoretuneapi.artist.model.dto.ArtistDto;
import dr.dev.scoretuneapi.artist.persistence.ArtistDao;
import dr.dev.scoretuneapi.core.exception.ArtistException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ArtistServiceImpl implements ArtistService {

    private final ArtistDao artistDao;

    public ArtistServiceImpl(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArtistDto> getAllArtists() {
        return artistDao.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ArtistDto getArtistById(UUID id) {
        Artist artist = artistDao.findById(id)
                .orElseThrow(() -> new ArtistException(ArtistException.Code.NOT_FOUND, null, "Artist not found with id: " + id));
        return toDto(artist);
    }

    @Override
    public ArtistDto createArtist(ArtistDto artistDto) {
        Artist artist = new Artist.Builder()
                .withName(artistDto.name())
                .withType(artistDto.type())
                .withPhotoLink(artistDto.photoLink())
                .build();
        Artist savedArtist = artistDao.save(artist);
        return toDto(savedArtist);
    }

    @Override
    public ArtistDto updateArtist(UUID id, ArtistDto artistDto) {
        if (!artistDao.findById(id).isPresent()) {
            throw new ArtistException(ArtistException.Code.NOT_FOUND, null, "Artist not found with id: " + id);
        }
        
        Artist artist = new Artist.Builder()
                .withId(id)
                .withName(artistDto.name())
                .withType(artistDto.type())
                .withPhotoLink(artistDto.photoLink())
                .build();
        
        Artist updatedArtist = artistDao.save(artist);
        return toDto(updatedArtist);
    }

    @Override
    public void deleteArtist(UUID id) {
        if (!artistDao.findById(id).isPresent()) {
            throw new ArtistException(ArtistException.Code.NOT_FOUND, null, "Artist not found with id: " + id);
        }
        artistDao.deleteById(id);
    }

    private ArtistDto toDto(Artist artist) {
        return new ArtistDto(
                artist.getId(),
                artist.getName(),
                artist.getType(),
                artist.getPhotoLink()
        );
    }
}
