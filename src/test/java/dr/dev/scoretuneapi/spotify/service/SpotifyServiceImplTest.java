package dr.dev.scoretuneapi.spotify.service;

import dr.dev.scoretuneapi.core.exception.SpotifyException;
import dr.dev.scoretuneapi.spotify.connector.SpotifyConnector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpotifyServiceImplTest {

    @Mock
    private SpotifyConnector spotifyConnector;

    @InjectMocks
    private SpotifyServiceImpl spotifyService;

    @Test
    void givenArtistName_whenGetArtistPhoto_thenReturnPhotoUrl() {
        when(spotifyConnector.findArtistPhotoUrl("The Weeknd"))
                .thenReturn("https://i.scdn.co/image/example.jpg");

        var result = spotifyService.getArtistPhoto("The Weeknd");

        assertThat(result.photoUrl()).isEqualTo("https://i.scdn.co/image/example.jpg");
        verify(spotifyConnector).findArtistPhotoUrl("The Weeknd");
    }

    @Test
    void givenBlankName_whenGetArtistPhoto_thenThrowNameRequired() {
        assertThatThrownBy(() -> spotifyService.getArtistPhoto("  "))
                .isInstanceOf(SpotifyException.class)
                .extracting("code")
                .isEqualTo(SpotifyException.Code.NAME_REQUIRED);
    }

    @Test
    void givenProjectName_whenGetProjectCover_thenReturnCoverUrl() {
        when(spotifyConnector.findProjectCoverUrl("Drapeau noir", null))
                .thenReturn("https://i.scdn.co/image/cover.jpg");

        var result = spotifyService.getProjectCover("Drapeau noir", null);

        assertThat(result.coverUrl()).isEqualTo("https://i.scdn.co/image/cover.jpg");
        verify(spotifyConnector).findProjectCoverUrl("Drapeau noir", null);
    }

    @Test
    void givenProjectNameAndArtists_whenGetProjectCover_thenReturnCoverUrl() {
        when(spotifyConnector.findProjectCoverUrl("NFL", List.of("Vald")))
                .thenReturn("https://i.scdn.co/image/cover.jpg");

        var result = spotifyService.getProjectCover("NFL", List.of("Vald"));

        assertThat(result.coverUrl()).isEqualTo("https://i.scdn.co/image/cover.jpg");
        verify(spotifyConnector).findProjectCoverUrl("NFL", List.of("Vald"));
    }

    @Test
    void givenBlankProjectName_whenGetProjectCover_thenThrowNameRequired() {
        assertThatThrownBy(() -> spotifyService.getProjectCover("  ", null))
                .isInstanceOf(SpotifyException.class)
                .extracting("code")
                .isEqualTo(SpotifyException.Code.NAME_REQUIRED);
    }
}
