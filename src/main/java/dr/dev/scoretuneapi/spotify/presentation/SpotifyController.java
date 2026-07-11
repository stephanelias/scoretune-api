package dr.dev.scoretuneapi.spotify.presentation;

import dr.dev.scoretuneapi.spotify.model.dto.SpotifyArtistPhotoDto;
import dr.dev.scoretuneapi.spotify.service.SpotifyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/spotify")
@RestController
public class SpotifyController {

    private final SpotifyService spotifyService;

    public SpotifyController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/artist/photo")
    public ResponseEntity<SpotifyArtistPhotoDto> getArtistPhoto(@RequestParam String name) {
        return ResponseEntity.ok(spotifyService.getArtistPhoto(name));
    }
}
