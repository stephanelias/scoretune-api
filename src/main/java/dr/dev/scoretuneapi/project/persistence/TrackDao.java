package dr.dev.scoretuneapi.project.persistence;

import dr.dev.scoretuneapi.project.model.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TrackDao {

    Page<Track> findFeaturingTracksByArtistId(UUID artistId, Pageable pageable);
}
