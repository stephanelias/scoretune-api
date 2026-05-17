package dr.dev.scoretuneapi.artist.persistence;

import dr.dev.scoretuneapi.artist.model.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ArtistDao {

    Optional<Artist> findById(UUID id);

    Page<Artist> findAll(Pageable pageable);

    Page<Artist> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Artist save(Artist artist);

    void deleteById(UUID id);
}
