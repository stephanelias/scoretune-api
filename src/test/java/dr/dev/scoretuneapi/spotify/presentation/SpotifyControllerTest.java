package dr.dev.scoretuneapi.spotify.presentation;

import dr.dev.scoretuneapi.core.exception.SpotifyException;
import dr.dev.scoretuneapi.spotify.model.dto.SpotifyArtistPhotoDto;
import dr.dev.scoretuneapi.spotify.service.SpotifyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SpotifyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpotifyService spotifyService;

    @Test
    @WithMockUser(roles = "MODO")
    void givenModoUser_whenGetArtistPhoto_thenReturnPhotoUrl() throws Exception {
        when(spotifyService.getArtistPhoto("The Weeknd"))
                .thenReturn(new SpotifyArtistPhotoDto("https://i.scdn.co/image/example.jpg"));

        mockMvc.perform(get("/api/spotify/artist/photo").param("name", "The Weeknd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photoUrl").value("https://i.scdn.co/image/example.jpg"));

        verify(spotifyService).getArtistPhoto("The Weeknd");
    }

    @Test
    void givenUnauthenticatedUser_whenGetArtistPhoto_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/spotify/artist/photo").param("name", "The Weeknd"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenRegularUser_whenGetArtistPhoto_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/spotify/artist/photo").param("name", "The Weeknd"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MODO")
    void givenArtistNotFound_whenGetArtistPhoto_thenReturnNotFound() throws Exception {
        when(spotifyService.getArtistPhoto("Unknown Artist"))
                .thenThrow(new SpotifyException(SpotifyException.Code.ARTIST_NOT_FOUND, null));

        mockMvc.perform(get("/api/spotify/artist/photo").param("name", "Unknown Artist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("spotify.artist-not-found"));
    }
}
