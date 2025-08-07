package dr.dev.scoretuneapi.core.exception;

import dr.dev.scoretuneapi.user.model.User;
import org.springframework.http.HttpStatus;

public class UserException extends ApiException {

    public enum Code {
        ALREADY_EXISTS("user.already-exists", HttpStatus.CONFLICT, "Article not found for given id") ;

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
    public final User user;

    public UserException(Code code, User user) {
        this(code, user, code.message);
    }

    public UserException(Code code, User user, String message) {
        super(message, null, code.status, code.code, null, user);
        this.code = code;
        this.user = user;
    }

}
