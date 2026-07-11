package dr.dev.scoretuneapi.core.exception;

import org.springframework.http.HttpStatus;

public class SpotifyException extends ApiException {

    public enum Code {
        NAME_REQUIRED("spotify.name-required", HttpStatus.BAD_REQUEST, "Name is required"),
        ARTIST_NOT_FOUND("spotify.artist-not-found", HttpStatus.NOT_FOUND, "No artist found on Spotify"),
        PROJECT_NOT_FOUND("spotify.project-not-found", HttpStatus.NOT_FOUND, "No project found on Spotify"),
        NOT_CONFIGURED("spotify.not-configured", HttpStatus.SERVICE_UNAVAILABLE, "Spotify integration is not configured"),
        API_ERROR("spotify.api-error", HttpStatus.BAD_GATEWAY, "Spotify API is unavailable");

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

    public SpotifyException(Code code, Object data) {
        this(code, data, code.message);
    }

    public SpotifyException(Code code, Object data, String message) {
        super(message, null, code.status, code.code, null, data);
        this.code = code;
    }

    public SpotifyException(Code code, Object data, String message, Throwable cause) {
        super(cause, new ExceptionEntity.Builder()
                .message(message)
                .status(code.status)
                .code(code.code)
                .data(data)
                .build());
        this.code = code;
    }
}
