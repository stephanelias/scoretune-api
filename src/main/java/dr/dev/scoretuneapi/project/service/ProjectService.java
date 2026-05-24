package dr.dev.scoretuneapi.project.service;

import dr.dev.scoretuneapi.core.dto.PageResponse;
import dr.dev.scoretuneapi.project.model.dto.ProjectDto;
import dr.dev.scoretuneapi.project.model.dto.ProjectRequestDto;
import dr.dev.scoretuneapi.project.model.dto.ProjectSummaryDto;

import java.util.UUID;

public interface ProjectService {

    PageResponse<ProjectSummaryDto> searchProjects(int page, int size, String search);

    ProjectDto getProjectById(UUID id);

    ProjectDto createProject(ProjectRequestDto request);

    ProjectDto updateProject(UUID id, ProjectRequestDto request);

    void deleteProject(UUID id);
}
