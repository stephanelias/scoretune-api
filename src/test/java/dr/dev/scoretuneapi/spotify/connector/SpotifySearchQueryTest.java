package dr.dev.scoretuneapi.spotify.connector;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SpotifySearchQueryTest {

    @Test
    void givenProjectNameOnly_whenAlbum_thenBuildAlbumQuery() {
        assertThat(SpotifySearchQuery.album("NFL", null))
                .isEqualTo("NFL");
    }

    @Test
    void givenProjectNameAndArtist_whenAlbum_thenJoinTermsWithSpaces() {
        assertThat(SpotifySearchQuery.album("Drapeau noir", List.of("Vald")))
                .isEqualTo("Drapeau noir Vald");
    }

    @Test
    void givenMultipleArtists_whenAlbum_thenJoinAllTermsWithSpaces() {
        assertThat(SpotifySearchQuery.album("Drip Harder", List.of("Lil Baby", "Gunna")))
                .isEqualTo("Drip Harder Lil Baby Gunna");
    }
}
