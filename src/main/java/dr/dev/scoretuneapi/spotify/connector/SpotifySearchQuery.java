package dr.dev.scoretuneapi.spotify.connector;

import java.util.ArrayList;
import java.util.List;

final class SpotifySearchQuery {

    private SpotifySearchQuery() {
    }

    static String album(String projectName, List<String> artistNames) {
        List<String> terms = new ArrayList<>();
        terms.add(projectName.trim());

        if (artistNames != null) {
            artistNames.stream()
                    .filter(name -> name != null && !name.isBlank())
                    .map(String::trim)
                    .forEach(terms::add);
        }

        return String.join(" ", terms);
    }
}
