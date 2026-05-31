package dr.dev.scoretuneapi.project.persistence;

import dr.dev.scoretuneapi.project.model.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrackDaoImpl extends TrackDao, JpaRepository<Track, UUID> {

    @Override
    @EntityGraph(attributePaths = {"project", "trackArtists", "trackArtists.artist"})
    @Query(
            value = """
                    SELECT DISTINCT t FROM Track t
                    JOIN t.project p
                    JOIN t.trackArtists ta
                    WHERE ta.artist.id = :artistId
                    AND ta.role = dr.dev.scoretuneapi.project.model.TrackArtistRole.FEATURING
                    ORDER BY p.releaseDate DESC, t.trackNumber ASC
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT t) FROM Track t
                    JOIN t.trackArtists ta
                    WHERE ta.artist.id = :artistId
                    AND ta.role = dr.dev.scoretuneapi.project.model.TrackArtistRole.FEATURING
                    """
    )
    Page<Track> findFeaturingTracksByArtistId(@Param("artistId") UUID artistId, Pageable pageable);
}
