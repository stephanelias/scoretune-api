package dr.dev.scoretuneapi.core.exception;

import dr.dev.scoretuneapi.project.model.Project;
import org.springframework.http.HttpStatus;

public class ProjectException extends ApiException {

    public enum Code {
        NOT_FOUND("project.not-found", HttpStatus.NOT_FOUND, "Projet introuvable"),
        ALREADY_EXISTS(
                "project.already-exists",
                HttpStatus.CONFLICT,
                "Un projet avec ce nom et cette date de sortie existe déjà"
        ),
        ARTIST_NOT_FOUND("project.artist-not-found", HttpStatus.BAD_REQUEST, "Artiste introuvable"),
        INVALID_TRACKS("project.invalid-tracks", HttpStatus.BAD_REQUEST, "Tracklist invalide"),
        DUPLICATE_TRACK_ARTIST(
                "project.duplicate-track-artist",
                HttpStatus.BAD_REQUEST,
                "Un artiste ne peut pas être à la fois interprète et featuring sur le même titre"
        );

        private final String code;
        private final HttpStatus status;
        private final String message;

        Code(String code, HttpStatus status, String message) {
            this.code = code;
            this.status = status;
            this.message = message;
        }
    }

    public final Code code;
    public final Project project;

    public ProjectException(Code code, Project project) {
        this(code, project, code.message);
    }

    public ProjectException(Code code, Project project, String message) {
        super(message, null, code.status, code.code, null, project);
        this.code = code;
        this.project = project;
    }
}
