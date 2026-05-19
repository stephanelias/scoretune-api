package dr.dev.scoretuneapi.artist.persistence;

import dr.dev.scoretuneapi.artist.model.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtistDaoImpl extends ArtistDao, JpaRepository<Artist, UUID> {
    @Override
    Optional<Artist> findById(UUID id);

    @Override
    Page<Artist> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
}
