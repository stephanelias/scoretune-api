package dr.dev.scoretuneapi.project.persistence;

import dr.dev.scoretuneapi.project.model.Project;
import dr.dev.scoretuneapi.project.model.ProjectType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectDaoImpl extends ProjectDao, JpaRepository<Project, UUID> {

    @Override
    @EntityGraph(attributePaths = {"artists"})
    Page<Project> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"artists", "tracks"})
    Optional<Project> findDetailedById(UUID id);

    @Override
    @EntityGraph(attributePaths = {"artists"})
    Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"artists"})
    Page<Project> findByArtists_IdAndType(UUID artistId, ProjectType type, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"artists"})
    @Query("""
            SELECT DISTINCT p FROM Project p
            JOIN p.tracks t
            JOIN t.trackArtists ta
            WHERE ta.artist.id = :artistId AND ta.role = dr.dev.scoretuneapi.project.model.TrackArtistRole.FEATURING
            """)
    Page<Project> findFeaturingProjectsByArtistId(@Param("artistId") UUID artistId, Pageable pageable);

    @Override
    boolean existsByNameIgnoreCaseAndReleaseDate(String name, LocalDate releaseDate);

    @Override
    boolean existsByNameIgnoreCaseAndReleaseDateAndIdNot(String name, LocalDate releaseDate, UUID id);
}
