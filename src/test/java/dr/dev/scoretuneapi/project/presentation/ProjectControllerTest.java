package dr.dev.scoretuneapi.project.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import dr.dev.scoretuneapi.core.dto.PageResponse;
import dr.dev.scoretuneapi.project.model.ProjectCategory;
import dr.dev.scoretuneapi.project.model.ProjectType;
import dr.dev.scoretuneapi.project.model.ProjectZone;
import dr.dev.scoretuneapi.project.model.dto.ProjectDto;
import dr.dev.scoretuneapi.project.model.dto.ProjectRequestDto;
import dr.dev.scoretuneapi.project.model.dto.ProjectSummaryDto;
import dr.dev.scoretuneapi.project.model.dto.TrackRequestDto;
import dr.dev.scoretuneapi.project.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID projectId;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        projectDto = new ProjectDto(
                projectId,
                "Drapeau noir",
                LocalDate.of(2026, 5, 20),
                ProjectType.SINGLE,
                ProjectCategory.HIP_HOP_RAP,
                ProjectZone.FR,
                null,
                List.of(),
                List.of()
        );
    }

    @Test
    void givenProjectsExist_whenSearchProjects_thenReturnPagedProjects() throws Exception {
        PageResponse<ProjectSummaryDto> page = new PageResponse<>(
                List.of(new ProjectSummaryDto(
                        projectId,
                        "Drapeau noir",
                        LocalDate.of(2026, 5, 20),
                        ProjectType.SINGLE,
                        ProjectCategory.HIP_HOP_RAP,
                        ProjectZone.FR,
                        null,
                        List.of()
                )),
                0,
                12,
                1,
                1
        );

        when(projectService.searchProjects(0, 12, null)).thenReturn(page);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Drapeau noir"));

        verify(projectService).searchProjects(0, 12, null);
    }

    @Test
    @WithMockUser(roles = "MODO")
    void givenModoUser_whenCreateProject_thenReturnCreated() throws Exception {
        ProjectRequestDto request = new ProjectRequestDto(
                "Drapeau noir",
                LocalDate.of(2026, 5, 20),
                ProjectType.SINGLE,
                ProjectCategory.HIP_HOP_RAP,
                ProjectZone.FR,
                null,
                List.of(),
                List.of(new TrackRequestDto(1, "Track 1", List.of(UUID.randomUUID()), List.of()))
        );

        when(projectService.createProject(any(ProjectRequestDto.class))).thenReturn(projectDto);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Drapeau noir"));

        verify(projectService).createProject(any(ProjectRequestDto.class));
    }
}
