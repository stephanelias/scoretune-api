package dr.dev.scoretuneapi.artist.presentation;

import dr.dev.scoretuneapi.artist.model.dto.ArtistDto;
import dr.dev.scoretuneapi.artist.service.ArtistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/artists")
@RestController
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public ResponseEntity<List<ArtistDto>> getAllArtists() {
        List<ArtistDto> artists = artistService.getAllArtists();
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistDto> getArtistById(@PathVariable UUID id) {
        ArtistDto artist = artistService.getArtistById(id);
        return ResponseEntity.ok(artist);
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
