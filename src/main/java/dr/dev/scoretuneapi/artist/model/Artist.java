package dr.dev.scoretuneapi.artist.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "artists")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArtistType type;

    @Column(name = "photo_link", nullable = true)
    private String photoLink;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArtistType getType() {
        return type;
    }

    public void setType(ArtistType type) {
        this.type = type;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public static final class Builder {
        private UUID id;
        private String name;
        private ArtistType type;
        private String photoLink;

        public Builder() {
        }

        public static Builder anArtist() {
            return new Builder();
        }

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withType(ArtistType type) {
            this.type = type;
            return this;
        }

        public Builder withPhotoLink(String photoLink) {
            this.photoLink = photoLink;
            return this;
        }

        public Artist build() {
            Artist artist = new Artist();
            artist.setId(id);
            artist.setName(name);
            artist.setType(type);
            artist.setPhotoLink(photoLink);
            return artist;
        }
    }
}
