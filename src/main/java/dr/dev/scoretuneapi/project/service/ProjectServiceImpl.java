package dr.dev.scoretuneapi.project.service;

import dr.dev.scoretuneapi.artist.model.Artist;
import dr.dev.scoretuneapi.artist.persistence.ArtistDao;
import dr.dev.scoretuneapi.core.dto.PageResponse;
import dr.dev.scoretuneapi.core.exception.ProjectException;
import dr.dev.scoretuneapi.project.model.Project;
import dr.dev.scoretuneapi.project.model.ProjectType;
import dr.dev.scoretuneapi.project.model.Track;
import dr.dev.scoretuneapi.project.model.TrackArtist;
import dr.dev.scoretuneapi.project.model.TrackArtistRole;
import dr.dev.scoretuneapi.project.model.dto.ArtistSummaryDto;
import dr.dev.scoretuneapi.project.model.dto.ProjectDto;
import dr.dev.scoretuneapi.project.model.dto.ProjectRequestDto;
import dr.dev.scoretuneapi.project.model.dto.ProjectSummaryDto;
import dr.dev.scoretuneapi.project.model.dto.TrackDto;
import dr.dev.scoretuneapi.project.model.dto.TrackRequestDto;
import dr.dev.scoretuneapi.project.persistence.ProjectDao;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectDao projectDao;
    private final ArtistDao artistDao;
    private final EntityManager entityManager;

    public ProjectServiceImpl(ProjectDao projectDao, ArtistDao artistDao, EntityManager entityManager) {
        this.projectDao = projectDao;
        this.artistDao = artistDao;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProjectSummaryDto> searchProjects(int page, int size, String search) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.clamp(size, 1, 100);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("name").ascending());

        Page<Project> result = (search == null || search.isBlank())
                ? projectDao.findAll(pageable)
                : projectDao.findByNameContainingIgnoreCase(search.trim(), pageable);

        return new PageResponse<>(
                result.getContent().stream().map(this::toSummaryDto).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto getProjectById(UUID id) {
        Project project = projectDao.findDetailedById(id)
                .orElseThrow(() -> new ProjectException(ProjectException.Code.NOT_FOUND, null));
        return toDetailDto(project);
    }

    @Override
    public ProjectDto createProject(ProjectRequestDto request) {
        String name = request.name().trim();
        assertProjectIsAvailable(name, request.releaseDate(), null);
        validateTracks(request.type(), request.tracks());

        Map<UUID, Artist> artistsById = resolveArtists(collectArtistIds(request));

        Project project = buildProject(new Project(), request, artistsById);
        Project saved = projectDao.save(project);
        return projectDao.findDetailedById(saved.getId())
                .map(this::toDetailDto)
                .orElseThrow(() -> new ProjectException(ProjectException.Code.NOT_FOUND, null));
    }

    @Override
    public ProjectDto updateProject(UUID id, ProjectRequestDto request) {
        Project project = projectDao.findDetailedById(id)
                .orElseThrow(() -> new ProjectException(ProjectException.Code.NOT_FOUND, null));

        String name = request.name().trim();
        assertProjectIsAvailable(name, request.releaseDate(), id);
        validateTracks(request.type(), request.tracks());

        Map<UUID, Artist> artistsById = resolveArtists(collectArtistIds(request));

        project.getTracks().clear();
        project.getArtists().clear();
        entityManager.flush();

        project.setName(name);
        project.setReleaseDate(request.releaseDate());
        project.setType(request.type());
        project.setCategory(request.category());
        project.setZone(request.zone());
        project.setCoverLink(request.coverLink());

        populateAssociations(project, request, artistsById);

        projectDao.save(project);
        return projectDao.findDetailedById(id)
                .map(this::toDetailDto)
                .orElseThrow(() -> new ProjectException(ProjectException.Code.NOT_FOUND, null));
    }

    @Override
    public void deleteProject(UUID id) {
        if (!projectDao.findById(id).isPresent()) {
            throw new ProjectException(ProjectException.Code.NOT_FOUND, null);
        }
        projectDao.deleteById(id);
    }

    private void assertProjectIsAvailable(String name, java.time.LocalDate releaseDate, UUID excludeId) {
        boolean exists = excludeId == null
                ? projectDao.existsByNameIgnoreCaseAndReleaseDate(name, releaseDate)
                : projectDao.existsByNameIgnoreCaseAndReleaseDateAndIdNot(name, releaseDate, excludeId);

        if (exists) {
            throw new ProjectException(ProjectException.Code.ALREADY_EXISTS, null);
        }
    }

    private void validateTracks(ProjectType type, List<TrackRequestDto> tracks) {
        if (tracks == null || tracks.isEmpty()) {
            throw new ProjectException(ProjectException.Code.INVALID_TRACKS, null, "At least one track is required");
        }

        if (type == ProjectType.SINGLE && tracks.size() != 1) {
            throw new ProjectException(
                    ProjectException.Code.INVALID_TRACKS,
                    null,
                    "A single must contain exactly one track"
            );
        }

        Set<Integer> trackNumbers = new HashSet<>();
        for (TrackRequestDto track : tracks) {
            if (!trackNumbers.add(track.trackNumber())) {
                throw new ProjectException(
                        ProjectException.Code.INVALID_TRACKS,
                        null,
                        "Track numbers must be unique within the project"
                );
            }

            if (track.interpreterIds().isEmpty()) {
                throw new ProjectException(
                        ProjectException.Code.INVALID_TRACKS,
                        null,
                        "Each track must have at least one interpreter"
                );
            }

            for (UUID featuringId : track.featuringIds()) {
                if (track.interpreterIds().contains(featuringId)) {
                    throw new ProjectException(ProjectException.Code.DUPLICATE_TRACK_ARTIST, null);
                }
            }
        }
    }

    private Set<UUID> collectArtistIds(ProjectRequestDto request) {
        Set<UUID> ids = new LinkedHashSet<>(request.artistIds());
        for (TrackRequestDto track : request.tracks()) {
            ids.addAll(track.interpreterIds());
            ids.addAll(track.featuringIds());
        }
        return ids;
    }

    private Map<UUID, Artist> resolveArtists(Set<UUID> artistIds) {
        if (artistIds.isEmpty()) {
            return Map.of();
        }

        List<Artist> artists = artistDao.findAllByIdIn(artistIds);
        if (artists.size() != artistIds.size()) {
            throw new ProjectException(ProjectException.Code.ARTIST_NOT_FOUND, null);
        }

        return artists.stream().collect(Collectors.toMap(Artist::getId, Function.identity()));
    }

    private Project buildProject(Project project, ProjectRequestDto request, Map<UUID, Artist> artistsById) {
        project.setName(request.name().trim());
        project.setReleaseDate(request.releaseDate());
        project.setType(request.type());
        project.setCategory(request.category());
        project.setZone(request.zone());
        project.setCoverLink(request.coverLink());
        populateAssociations(project, request, artistsById);
        return project;
    }

    private void populateAssociations(Project project, ProjectRequestDto request, Map<UUID, Artist> artistsById) {
        Set<Artist> projectArtists = request.artistIds().stream()
                .map(artistsById::get)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        project.setArtists(projectArtists);

        List<TrackRequestDto> sortedTracks = request.tracks().stream()
                .sorted(Comparator.comparing(TrackRequestDto::trackNumber))
                .toList();

        for (TrackRequestDto trackRequest : sortedTracks) {
            Track track = new Track();
            track.setTrackNumber(trackRequest.trackNumber());
            track.setName(trackRequest.name().trim());

            for (UUID interpreterId : trackRequest.interpreterIds()) {
                TrackArtist trackArtist = new TrackArtist();
                trackArtist.setArtist(artistsById.get(interpreterId));
                trackArtist.setRole(TrackArtistRole.INTERPRETER);
                track.addTrackArtist(trackArtist);
            }

            for (UUID featuringId : trackRequest.featuringIds()) {
                TrackArtist trackArtist = new TrackArtist();
                trackArtist.setArtist(artistsById.get(featuringId));
                trackArtist.setRole(TrackArtistRole.FEATURING);
                track.addTrackArtist(trackArtist);
            }

            project.addTrack(track);
        }
    }

    private ProjectSummaryDto toSummaryDto(Project project) {
        List<ArtistSummaryDto> artists = project.getArtists().stream()
                .sorted(Comparator.comparing(Artist::getName, String.CASE_INSENSITIVE_ORDER))
                .map(this::toArtistSummaryDto)
                .toList();

        return new ProjectSummaryDto(
                project.getId(),
                project.getName(),
                project.getReleaseDate(),
                project.getType(),
                project.getCategory(),
                project.getZone(),
                project.getCoverLink(),
                artists
        );
    }

    private ProjectDto toDetailDto(Project project) {
        List<ArtistSummaryDto> artists = project.getArtists().stream()
                .sorted(Comparator.comparing(Artist::getName, String.CASE_INSENSITIVE_ORDER))
                .map(this::toArtistSummaryDto)
                .toList();

        List<TrackDto> tracks = project.getTracks().stream()
                .sorted(Comparator.comparing(Track::getTrackNumber))
                .map(this::toTrackDto)
                .toList();

        return new ProjectDto(
                project.getId(),
                project.getName(),
                project.getReleaseDate(),
                project.getType(),
                project.getCategory(),
                project.getZone(),
                project.getCoverLink(),
                artists,
                tracks
        );
    }

    private TrackDto toTrackDto(Track track) {
        List<ArtistSummaryDto> interpreters = new ArrayList<>();
        List<ArtistSummaryDto> featurings = new ArrayList<>();

        for (TrackArtist trackArtist : track.getTrackArtists()) {
            ArtistSummaryDto artistDto = toArtistSummaryDto(trackArtist.getArtist());
            if (trackArtist.getRole() == TrackArtistRole.INTERPRETER) {
                interpreters.add(artistDto);
            } else {
                featurings.add(artistDto);
            }
        }

        interpreters.sort(Comparator.comparing(ArtistSummaryDto::name, String.CASE_INSENSITIVE_ORDER));
        featurings.sort(Comparator.comparing(ArtistSummaryDto::name, String.CASE_INSENSITIVE_ORDER));

        return new TrackDto(
                track.getId(),
                track.getTrackNumber(),
                track.getName(),
                interpreters,
                featurings
        );
    }

    private ArtistSummaryDto toArtistSummaryDto(Artist artist) {
        return new ArtistSummaryDto(
                artist.getId(),
                artist.getName(),
                artist.getType(),
                artist.getPhotoLink()
        );
    }
}
