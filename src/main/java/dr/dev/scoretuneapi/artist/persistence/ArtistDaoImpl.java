package dr.dev.scoretuneapi.artist.persistence;

import dr.dev.scoretuneapi.artist.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtistDaoImpl extends ArtistDao, JpaRepository<Artist, UUID> {
    @Override
    Optional<Artist> findById(UUID id);
}
