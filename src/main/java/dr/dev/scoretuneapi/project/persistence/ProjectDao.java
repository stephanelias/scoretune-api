package dr.dev.scoretuneapi.project.persistence;

import dr.dev.scoretuneapi.project.model.Project;
import dr.dev.scoretuneapi.project.model.ProjectType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ProjectDao {

    Optional<Project> findById(UUID id);

    Optional<Project> findDetailedById(UUID id);

    Page<Project> findAll(Pageable pageable);

    Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Project> findByArtists_IdAndType(UUID artistId, ProjectType type, Pageable pageable);

    Page<Project> findFeaturingProjectsByArtistId(UUID artistId, Pageable pageable);

    boolean existsByNameIgnoreCaseAndReleaseDate(String name, LocalDate releaseDate);

    boolean existsByNameIgnoreCaseAndReleaseDateAndIdNot(String name, LocalDate releaseDate, UUID id);

    Project save(Project project);

    void deleteById(UUID id);
}
