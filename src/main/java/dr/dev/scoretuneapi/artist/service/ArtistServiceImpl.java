package dr.dev.scoretuneapi.artist.service;

import dr.dev.scoretuneapi.artist.model.Artist;
import dr.dev.scoretuneapi.artist.model.dto.ArtistDto;
import dr.dev.scoretuneapi.artist.persistence.ArtistDao;
import dr.dev.scoretuneapi.core.dto.PageResponse;
import dr.dev.scoretuneapi.core.exception.ArtistException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PageResponse<ArtistDto> searchArtists(int page, int size, String search) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.clamp(size, 1, 100);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("name").ascending());

        Page<Artist> result = (search == null || search.isBlank())
                ? artistDao.findAll(pageable)
                : artistDao.findByNameContainingIgnoreCase(search.trim(), pageable);

        return new PageResponse<>(
                result.getContent().stream().map(this::toDto).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
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
        String name = artistDto.name().trim();
        assertNameIsAvailable(name, null);

        Artist artist = new Artist.Builder()
                .withName(name)
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

        String name = artistDto.name().trim();
        assertNameIsAvailable(name, id);

        Artist artist = new Artist.Builder()
                .withId(id)
                .withName(name)
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

    private void assertNameIsAvailable(String name, UUID excludeId) {
        boolean nameTaken = excludeId == null
                ? artistDao.existsByNameIgnoreCase(name)
                : artistDao.existsByNameIgnoreCaseAndIdNot(name, excludeId);

        if (nameTaken) {
            throw new ArtistException(ArtistException.Code.NAME_ALREADY_EXISTS, null);
        }
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
