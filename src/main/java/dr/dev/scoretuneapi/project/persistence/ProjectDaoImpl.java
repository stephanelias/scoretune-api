package dr.dev.scoretuneapi.project.persistence;

import dr.dev.scoretuneapi.project.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectDaoImpl extends ProjectDao, JpaRepository<Project, UUID> {

    @Override
    @EntityGraph(attributePaths = {"artists", "tracks"})
    Optional<Project> findDetailedById(UUID id);

    @Override
    Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Override
    boolean existsByNameIgnoreCaseAndReleaseDate(String name, LocalDate releaseDate);

    @Override
    boolean existsByNameIgnoreCaseAndReleaseDateAndIdNot(String name, LocalDate releaseDate, UUID id);
}
