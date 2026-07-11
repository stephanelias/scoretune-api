package dr.dev.scoretuneapi.spotify.connector;

import dr.dev.scoretuneapi.core.exception.SpotifyException;
import dr.dev.scoretuneapi.spotify.config.SpotifyProperties;
import dr.dev.scoretuneapi.spotify.connector.dto.SpotifySearchArtistsResponse;
import dr.dev.scoretuneapi.spotify.connector.dto.SpotifyTokenResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Component
public class SpotifyConnector {

    private final RestClient spotifyApiRestClient;
    private final RestClient spotifyAuthRestClient;
    private final SpotifyProperties properties;

    private volatile String accessToken;
    private volatile Instant tokenExpiresAt = Instant.EPOCH;

    public SpotifyConnector(
            @Qualifier("spotifyApiRestClient") RestClient spotifyApiRestClient,
            @Qualifier("spotifyAuthRestClient") RestClient spotifyAuthRestClient,
            SpotifyProperties properties
    ) {
        this.spotifyApiRestClient = spotifyApiRestClient;
        this.spotifyAuthRestClient = spotifyAuthRestClient;
        this.properties = properties;
    }

    public String findArtistPhotoUrl(String artistName) {
        if (!properties.isConfigured()) {
            throw new SpotifyException(SpotifyException.Code.NOT_CONFIGURED, null);
        }

        try {
            ensureAccessToken();

            SpotifySearchArtistsResponse response = spotifyApiRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", artistName)
                            .queryParam("type", "artist")
                            .queryParam("limit", 1)
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(SpotifySearchArtistsResponse.class);

            return extractPhotoUrl(response);
        } catch (RestClientException exception) {
            throw new SpotifyException(
                    SpotifyException.Code.API_ERROR,
                    null,
                    "Spotify API request failed",
                    exception
            );
        }
    }

    private void ensureAccessToken() {
        if (accessToken != null && Instant.now().isBefore(tokenExpiresAt)) {
            return;
        }

        synchronized (this) {
            if (accessToken != null && Instant.now().isBefore(tokenExpiresAt)) {
                return;
            }

            String credentials = properties.getClientId() + ":" + properties.getClientSecret();
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");

            SpotifyTokenResponse tokenResponse = spotifyAuthRestClient.post()
                    .uri(properties.getAuthUrl())
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(SpotifyTokenResponse.class);

            if (tokenResponse == null || tokenResponse.accessToken() == null || tokenResponse.accessToken().isBlank()) {
                throw new SpotifyException(SpotifyException.Code.API_ERROR, null, "Spotify authentication failed");
            }

            accessToken = tokenResponse.accessToken();
            long expiresInSeconds = tokenResponse.expiresIn() != null ? tokenResponse.expiresIn() : 3600;
            tokenExpiresAt = Instant.now().plusSeconds(Math.max(expiresInSeconds - 60, 60));
        }
    }

    private String extractPhotoUrl(SpotifySearchArtistsResponse response) {
        if (response == null
                || response.artists() == null
                || response.artists().items() == null
                || response.artists().items().isEmpty()) {
            throw new SpotifyException(SpotifyException.Code.ARTIST_NOT_FOUND, null);
        }

        List<SpotifySearchArtistsResponse.Image> images = response.artists().items().getFirst().images();
        if (images == null || images.isEmpty()) {
            throw new SpotifyException(SpotifyException.Code.ARTIST_NOT_FOUND, null, "No photo available for this artist");
        }

        return images.stream()
                .map(SpotifySearchArtistsResponse.Image::url)
                .filter(url -> url != null && !url.isBlank())
                .findFirst()
                .orElseThrow(() -> new SpotifyException(SpotifyException.Code.ARTIST_NOT_FOUND, null, "No photo available for this artist"));
    }
}
