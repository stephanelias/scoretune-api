package dr.dev.scoretuneapi.project.presentation;

import dr.dev.scoretuneapi.core.dto.PageResponse;
import dr.dev.scoretuneapi.project.model.dto.ProjectDto;
import dr.dev.scoretuneapi.project.model.dto.ProjectRequestDto;
import dr.dev.scoretuneapi.project.model.dto.ProjectSummaryDto;
import dr.dev.scoretuneapi.project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/projects")
@RestController
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProjectSummaryDto>> searchProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(projectService.searchProjects(page, size, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody ProjectRequestDto request
    ) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
