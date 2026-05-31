package dr.dev.scoretuneapi.artist.presentation;

import dr.dev.scoretuneapi.artist.model.dto.ArtistDto;
import dr.dev.scoretuneapi.artist.service.ArtistService;
import dr.dev.scoretuneapi.core.dto.PageResponse;
import dr.dev.scoretuneapi.project.model.ProjectType;
import dr.dev.scoretuneapi.project.model.dto.ProjectAppearanceDto;
import dr.dev.scoretuneapi.project.model.dto.ProjectSummaryDto;
import dr.dev.scoretuneapi.project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/artists")
@RestController
public class ArtistController {

    private final ArtistService artistService;
    private final ProjectService projectService;

    public ArtistController(ArtistService artistService, ProjectService projectService) {
        this.artistService = artistService;
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ArtistDto>> searchArtists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String search
    ) {
        PageResponse<ArtistDto> artists = artistService.searchArtists(page, size, search);
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistDto> getArtistById(@PathVariable UUID id) {
        ArtistDto artist = artistService.getArtistById(id);
        return ResponseEntity.ok(artist);
    }

    @GetMapping("/{id}/projects")
    public ResponseEntity<PageResponse<ProjectSummaryDto>> getArtistProjects(
            @PathVariable UUID id,
            @RequestParam ProjectType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size
    ) {
        return ResponseEntity.ok(projectService.searchProjectsByArtist(id, type, page, size));
    }

    @GetMapping("/{id}/appearances")
    public ResponseEntity<PageResponse<ProjectAppearanceDto>> getArtistAppearances(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(projectService.searchAppearancesByArtist(id, page, size));
    }

    @PostMapping
    public ResponseEntity<ArtistDto> createArtist(@Valid @RequestBody ArtistDto artistDto) {
        ArtistDto createdArtist = artistService.createArtist(artistDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArtist);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistDto> updateArtist(@PathVariable UUID id, @Valid @RequestBody ArtistDto artistDto) {
        ArtistDto updatedArtist = artistService.updateArtist(id, artistDto);
        return ResponseEntity.ok(updatedArtist);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable UUID id) {
        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }
}
