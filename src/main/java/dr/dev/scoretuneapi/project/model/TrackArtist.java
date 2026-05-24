package dr.dev.scoretuneapi.project.model;

import dr.dev.scoretuneapi.artist.model.Artist;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "track_artists",
        uniqueConstraints = @UniqueConstraint(columnNames = {"track_id", "artist_id"})
)
public class TrackArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrackArtistRole role;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public TrackArtistRole getRole() {
        return role;
    }

    public void setRole(TrackArtistRole role) {
        this.role = role;
    }
}
