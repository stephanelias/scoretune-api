package dr.dev.scoretuneapi.project.model;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "tracks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "track_number"})
)
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "track_number", nullable = false)
    private int trackNumber;

    @Column(nullable = false)
    private String name;

    @BatchSize(size = 50)
    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrackArtist> trackArtists = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TrackArtist> getTrackArtists() {
        return trackArtists;
    }

    public void setTrackArtists(List<TrackArtist> trackArtists) {
        this.trackArtists = trackArtists;
    }

    public void addTrackArtist(TrackArtist trackArtist) {
        trackArtists.add(trackArtist);
        trackArtist.setTrack(this);
    }
}
