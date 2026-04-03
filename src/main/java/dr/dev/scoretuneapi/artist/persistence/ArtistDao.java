package dr.dev.scoretuneapi.artist.persistence;

import dr.dev.scoretuneapi.artist.model.Artist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistDao {

    Optional<Artist> findById(UUID id);

    List<Artist> findAll();

    Artist save(Artist artist);

    void deleteById(UUID id);
}
