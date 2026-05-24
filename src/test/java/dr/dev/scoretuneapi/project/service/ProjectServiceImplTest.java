package dr.dev.scoretuneapi.project.service;

import dr.dev.scoretuneapi.artist.model.Artist;
import dr.dev.scoretuneapi.artist.model.ArtistType;
import dr.dev.scoretuneapi.artist.persistence.ArtistDao;
import dr.dev.scoretuneapi.core.exception.ProjectException;
import dr.dev.scoretuneapi.project.model.Project;
import dr.dev.scoretuneapi.project.model.ProjectCategory;
import dr.dev.scoretuneapi.project.model.ProjectType;
import dr.dev.scoretuneapi.project.model.ProjectZone;
import dr.dev.scoretuneapi.project.model.dto.ProjectRequestDto;
import dr.dev.scoretuneapi.project.model.dto.TrackRequestDto;
import dr.dev.scoretuneapi.project.persistence.ProjectDao;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectDao projectDao;

    @Mock
    private ArtistDao artistDao;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private UUID projectId;
    private UUID artistId;
    private Artist artist;
    private LocalDate releaseDate;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        artistId = UUID.randomUUID();
        releaseDate = LocalDate.of(2026, 5, 20);
        artist = Artist.Builder.anArtist()
                .withId(artistId)
                .withName("Booba")
                .withType(ArtistType.ARTIST)
                .build();
    }

    @Nested
    class CreateProjectTests {

        @Test
        void givenValidSingle_whenCreateProject_thenSaveProject() {
            ProjectRequestDto request = new ProjectRequestDto(
                    "Drapeau noir",
                    releaseDate,
                    ProjectType.SINGLE,
                    ProjectCategory.HIP_HOP_RAP,
                    ProjectZone.FR,
                    null,
                    List.of(artistId),
                    List.of(new TrackRequestDto(1, "Track 1", List.of(artistId), List.of()))
            );

            when(projectDao.existsByNameIgnoreCaseAndReleaseDate("Drapeau noir", releaseDate)).thenReturn(false);
            when(artistDao.findAllByIdIn(any())).thenReturn(List.of(artist));
            when(projectDao.save(any(Project.class))).thenAnswer(invocation -> {
                Project project = invocation.getArgument(0);
                project.setId(projectId);
                return project;
            });
            when(projectDao.findDetailedById(projectId)).thenAnswer(invocation -> {
                Project project = new Project();
                project.setId(projectId);
                project.setName("Drapeau noir");
                project.setReleaseDate(releaseDate);
                project.setType(ProjectType.SINGLE);
                project.setCategory(ProjectCategory.HIP_HOP_RAP);
                project.setZone(ProjectZone.FR);
                return Optional.of(project);
            });

            projectService.createProject(request);

            ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
            verify(projectDao).save(captor.capture());
            assertThat(captor.getValue().getTracks()).hasSize(1);
            assertThat(captor.getValue().getArtists()).hasSize(1);
        }

        @Test
        void givenDuplicateNameAndDate_whenCreateProject_thenThrowAlreadyExists() {
            ProjectRequestDto request = new ProjectRequestDto(
                    "Drapeau noir",
                    releaseDate,
                    ProjectType.SINGLE,
                    ProjectCategory.HIP_HOP_RAP,
                    ProjectZone.FR,
                    null,
                    List.of(),
                    List.of(new TrackRequestDto(1, "Track 1", List.of(artistId), List.of()))
            );

            when(projectDao.existsByNameIgnoreCaseAndReleaseDate("Drapeau noir", releaseDate)).thenReturn(true);

            assertThatThrownBy(() -> projectService.createProject(request))
                    .isInstanceOf(ProjectException.class)
                    .satisfies(ex -> assertThat(((ProjectException) ex).code)
                            .isEqualTo(ProjectException.Code.ALREADY_EXISTS));

            verify(projectDao, never()).save(any(Project.class));
        }

        @Test
        void givenInterpreterAndFeaturingSameArtist_whenCreateProject_thenThrowDuplicateTrackArtist() {
            ProjectRequestDto request = new ProjectRequestDto(
                    "Drapeau noir",
                    releaseDate,
                    ProjectType.SINGLE,
                    ProjectCategory.HIP_HOP_RAP,
                    ProjectZone.FR,
                    null,
                    List.of(),
                    List.of(new TrackRequestDto(1, "Track 1", List.of(artistId), List.of(artistId)))
            );

            when(projectDao.existsByNameIgnoreCaseAndReleaseDate("Drapeau noir", releaseDate)).thenReturn(false);

            assertThatThrownBy(() -> projectService.createProject(request))
                    .isInstanceOf(ProjectException.class)
                    .satisfies(ex -> assertThat(((ProjectException) ex).code)
                            .isEqualTo(ProjectException.Code.DUPLICATE_TRACK_ARTIST));

            verify(artistDao, never()).findAllByIdIn(any());
        }
    }
}
