package dr.dev.scoretuneapi.core.exception;

import dr.dev.scoretuneapi.artist.model.Artist;
import org.springframework.http.HttpStatus;

public class ArtistException extends ApiException {

    public enum Code {
        NOT_FOUND("artist.not-found", HttpStatus.NOT_FOUND, "Artist not found for given id"),
        NAME_ALREADY_EXISTS(
                "artist.name-already-exists",
                HttpStatus.CONFLICT,
                "Un artiste avec ce nom existe déjà"
        );

        private final String code;
        private final HttpStatus status;
        private final String message;

        Code(String code, HttpStatus status, String message) {
            this.code = code;
            this.status = status;
            this.message = message;
        }

        @Override
        public String toString() {
            return this.code;
        }

    }
    public final Code code;
    public final Artist artist;

    public ArtistException(Code code, Artist artist) {
        this(code, artist, code.message);
    }

    public ArtistException(Code code, Artist artist, String message) {
        super(message, null, code.status, code.code, null, artist);
        this.code = code;
        this.artist = artist;
    }

}
