package dr.dev.scoretuneapi.spotify.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(SpotifyProperties.class)
public class SpotifyConfig {

    @Bean("spotifyApiRestClient")
    RestClient spotifyApiRestClient(SpotifyProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getApiBaseUrl())
                .build();
    }

    @Bean("spotifyAuthRestClient")
    RestClient spotifyAuthRestClient() {
        return RestClient.create();
    }
}
