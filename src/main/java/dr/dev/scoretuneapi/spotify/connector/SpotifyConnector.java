package dr.dev.scoretuneapi.spotify.connector;

import dr.dev.scoretuneapi.core.exception.SpotifyException;
import dr.dev.scoretuneapi.spotify.config.SpotifyProperties;
import dr.dev.scoretuneapi.spotify.connector.dto.SpotifyAlbumTracksResponse;
import dr.dev.scoretuneapi.spotify.connector.dto.SpotifySearchAlbumsResponse;
import dr.dev.scoretuneapi.spotify.connector.dto.SpotifySearchArtistsResponse;
import dr.dev.scoretuneapi.spotify.connector.dto.SpotifyTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(SpotifyConnector.class);

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
            log.warn("Spotify integration is not configured");
            throw new SpotifyException(SpotifyException.Code.NOT_CONFIGURED, null);
        }

        try {
            ensureAccessToken();

            log.debug("Spotify search request: GET /search?q={}&type=artist&limit=1", artistName);

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

            String photoUrl = extractArtistPhotoUrl(response, artistName);
            log.debug("Spotify artist photo found for q={}", artistName);
            return photoUrl;
        } catch (SpotifyException exception) {
            throw exception;
        } catch (RestClientException exception) {
            log.error("Spotify API request failed: GET /search?q={}&type=artist&limit=1", artistName, exception);
            throw new SpotifyException(
                    SpotifyException.Code.API_ERROR,
                    null,
                    "Spotify API request failed",
                    exception
            );
        }
    }

    public String findProjectCoverUrl(String projectName, List<String> artistNames) {
        if (!properties.isConfigured()) {
            log.warn("Spotify integration is not configured");
            throw new SpotifyException(SpotifyException.Code.NOT_CONFIGURED, null);
        }

        String query = SpotifySearchQuery.album(projectName, artistNames);

        try {
            ensureAccessToken();

            log.debug("Spotify search request: GET /search?q={}&type=album&limit=1", query);

            SpotifySearchAlbumsResponse response = spotifyApiRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", query)
                            .queryParam("type", "album")
                            .queryParam("limit", 1)
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(SpotifySearchAlbumsResponse.class);

            String coverUrl = extractProjectCoverUrl(response, query);
            log.debug("Spotify project cover found for q={}", query);
            return coverUrl;
        } catch (SpotifyException exception) {
            throw exception;
        } catch (RestClientException exception) {
            log.error("Spotify API request failed: GET /search?q={}&type=album&limit=1", query, exception);
            throw new SpotifyException(
                    SpotifyException.Code.API_ERROR,
                    null,
                    "Spotify API request failed",
                    exception
            );
        }
    }

    public List<String> findProjectTracklist(String projectName, List<String> artistNames) {
        if (!properties.isConfigured()) {
            log.warn("Spotify integration is not configured");
            throw new SpotifyException(SpotifyException.Code.NOT_CONFIGURED, null);
        }

        String query = SpotifySearchQuery.album(projectName, artistNames);

        try {
            ensureAccessToken();

            log.debug("Spotify search request: GET /search?q={}&type=album&limit=1", query);

            SpotifySearchAlbumsResponse searchResponse = spotifyApiRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", query)
                            .queryParam("type", "album")
                            .queryParam("limit", 1)
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(SpotifySearchAlbumsResponse.class);

            String albumId = extractAlbumId(searchResponse, query);

            log.debug("Spotify album tracks request: GET /albums/{}/tracks", albumId);

            SpotifyAlbumTracksResponse tracksResponse = spotifyApiRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/albums/{id}/tracks")
                            .queryParam("limit", 50)
                            .build(albumId))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(SpotifyAlbumTracksResponse.class);

            List<String> trackNames = extractTrackNames(tracksResponse);
            log.debug("Spotify project tracklist found for q={}, tracks={}", query, trackNames.size());
            return trackNames;
        } catch (SpotifyException exception) {
            throw exception;
        } catch (RestClientException exception) {
            log.error("Spotify API request failed for project tracklist q={}", query, exception);
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

            log.debug("Spotify auth request: POST {}", properties.getAuthUrl());

            String credentials = properties.getClientId() + ":" + properties.getClientSecret();
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");

            try {
                SpotifyTokenResponse tokenResponse = spotifyAuthRestClient.post()
                        .uri(properties.getAuthUrl())
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body(body)
                        .retrieve()
                        .body(SpotifyTokenResponse.class);

                if (tokenResponse == null || tokenResponse.accessToken() == null || tokenResponse.accessToken().isBlank()) {
                    log.error("Spotify authentication failed: empty access token");
                    throw new SpotifyException(SpotifyException.Code.API_ERROR, null, "Spotify authentication failed");
                }

                accessToken = tokenResponse.accessToken();
                long expiresInSeconds = tokenResponse.expiresIn() != null ? tokenResponse.expiresIn() : 3600;
                tokenExpiresAt = Instant.now().plusSeconds(Math.max(expiresInSeconds - 60, 60));
                log.debug("Spotify access token refreshed, expires in {}s", expiresInSeconds);
            } catch (RestClientException exception) {
                log.error("Spotify authentication request failed: POST {}", properties.getAuthUrl(), exception);
                throw new SpotifyException(
                        SpotifyException.Code.API_ERROR,
                        null,
                        "Spotify authentication failed",
                        exception
                );
            }
        }
    }

    private String extractArtistPhotoUrl(SpotifySearchArtistsResponse response, String artistName) {
        if (response == null
                || response.artists() == null
                || response.artists().items() == null
                || response.artists().items().isEmpty()) {
            log.warn("Spotify artist not found for q={}", artistName);
            throw new SpotifyException(SpotifyException.Code.ARTIST_NOT_FOUND, null);
        }

        List<SpotifySearchArtistsResponse.Image> images = response.artists().items().getFirst().images();
        List<String> imageUrls = images == null
                ? List.of()
                : images.stream().map(SpotifySearchArtistsResponse.Image::url).toList();
        return extractFirstImageUrl(
                imageUrls,
                SpotifyException.Code.ARTIST_NOT_FOUND,
                "No photo available for this artist",
                "Spotify artist photo not available for q=" + artistName
        );
    }

    private String extractProjectCoverUrl(SpotifySearchAlbumsResponse response, String query) {
        if (response == null
                || response.albums() == null
                || response.albums().items() == null
                || response.albums().items().isEmpty()) {
            log.warn("Spotify project not found for q={}", query);
            throw new SpotifyException(SpotifyException.Code.PROJECT_NOT_FOUND, null);
        }

        List<SpotifySearchAlbumsResponse.Image> images = response.albums().items().getFirst().images();
        List<String> imageUrls = images == null
                ? List.of()
                : images.stream().map(SpotifySearchAlbumsResponse.Image::url).toList();
        return extractFirstImageUrl(
                imageUrls,
                SpotifyException.Code.PROJECT_NOT_FOUND,
                "No cover available for this project",
                "Spotify project cover not available for q=" + query
        );
    }

    private String extractAlbumId(SpotifySearchAlbumsResponse response, String query) {
        if (response == null
                || response.albums() == null
                || response.albums().items() == null
                || response.albums().items().isEmpty()) {
            log.warn("Spotify project not found for q={}", query);
            throw new SpotifyException(SpotifyException.Code.PROJECT_NOT_FOUND, null);
        }

        String albumId = response.albums().items().getFirst().id();
        if (albumId == null || albumId.isBlank()) {
            log.warn("Spotify album id missing for q={}", query);
            throw new SpotifyException(SpotifyException.Code.PROJECT_NOT_FOUND, null);
        }

        return albumId;
    }

    private List<String> extractTrackNames(SpotifyAlbumTracksResponse response) {
        if (response == null || response.items() == null || response.items().isEmpty()) {
            log.warn("Spotify album has no tracks");
            throw new SpotifyException(SpotifyException.Code.PROJECT_NOT_FOUND, null, "No tracks found on Spotify");
        }

        List<String> trackNames = response.items().stream()
                .map(SpotifyAlbumTracksResponse.TrackItem::name)
                .filter(name -> name != null && !name.isBlank())
                .toList();

        if (trackNames.isEmpty()) {
            log.warn("Spotify album tracks have no names");
            throw new SpotifyException(SpotifyException.Code.PROJECT_NOT_FOUND, null, "No tracks found on Spotify");
        }

        return trackNames;
    }

    private String extractFirstImageUrl(
            List<String> imageUrls,
            SpotifyException.Code notFoundCode,
            String notFoundMessage,
            String notFoundLogMessage
    ) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            log.warn(notFoundLogMessage);
            throw new SpotifyException(notFoundCode, null, notFoundMessage);
        }

        return imageUrls.stream()
                .filter(url -> url != null && !url.isBlank())
                .findFirst()
                .orElseThrow(() -> {
                    log.warn(notFoundLogMessage);
                    return new SpotifyException(notFoundCode, null, notFoundMessage);
                });
    }
}
