package dr.dev.scoretuneapi.project.service;

import dr.dev.scoretuneapi.core.dto.PageResponse;
import dr.dev.scoretuneapi.project.model.ProjectType;
import dr.dev.scoretuneapi.project.model.dto.ProjectAppearanceDto;
import dr.dev.scoretuneapi.project.model.dto.ProjectDto;
import dr.dev.scoretuneapi.project.model.dto.ProjectRequestDto;
import dr.dev.scoretuneapi.project.model.dto.ProjectSummaryDto;

import java.util.UUID;

public interface ProjectService {

    PageResponse<ProjectSummaryDto> searchProjects(int page, int size, String search);

    PageResponse<ProjectSummaryDto> searchProjectsByArtist(UUID artistId, ProjectType type, int page, int size);

    PageResponse<ProjectAppearanceDto> searchAppearancesByArtist(UUID artistId, int page, int size);

    ProjectDto getProjectById(UUID id);

    ProjectDto createProject(ProjectRequestDto request);

    ProjectDto updateProject(UUID id, ProjectRequestDto request);

    void deleteProject(UUID id);
}
