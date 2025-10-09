package dr.dev.scoretuneapi.core.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

public class ExceptionEntity {

    private LocalDateTime timestamp = LocalDateTime.now();
    @Nullable
    private String message;
    @Nullable
    @JsonIgnore
    private HttpStatus status;
    @Nullable
    private String code;
    @Nullable
    @JsonIgnore
    private HttpHeaders headers;
    @Nullable
    private Object data;
    @JsonIgnore
    private Throwable cause;

    public ExceptionEntity(ExceptionEntity exceptionEntity) {
        this.message = exceptionEntity.message;
        this.status = exceptionEntity.status;
        this.code = exceptionEntity.code;
        this.headers = exceptionEntity.headers;
        this.data = exceptionEntity.data;
    }

    public ExceptionEntity(@Nullable String message, @Nullable HttpStatus status, @Nullable String code, @Nullable HttpHeaders headers, @Nullable Object data, Throwable cause) {
        this.message = message;
        this.status = status;
        this.code = code;
        this.headers = headers;
        this.data = data;
        this.cause = cause;
    }

    public ResponseEntity<ExceptionEntity> toResponseEntity(WebRequest request) {
        return new ResponseEntity<>(this, headers, status);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Nullable
    public HttpStatus getStatus() {
        return status;
    }

    @Nullable
    public String getCode() {
        return code;
    }

    @Nullable
    public HttpHeaders getHeaders() {
        return headers;
    }

    @Nullable
    public Object getData() {
        return data;
    }

    public Throwable getCause() {
        return cause;
    }

    public static final class Builder {
        private LocalDateTime timestamp = LocalDateTime.now();
        private String message;
        private HttpStatus status;
        private String code;
        private HttpHeaders headers;
        private Object data;
        private Throwable cause;

        public Builder() {
        }

        public Builder(ExceptionEntity other) {
            this.timestamp = other.timestamp;
            this.message = other.message;
            this.status = other.status;
            this.code = other.code;
            this.headers = other.headers;
            this.data = other.data;
            this.cause = other.cause;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder headers(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Builder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        public ExceptionEntity build() {
            ExceptionEntity exceptionEntity = new ExceptionEntity(message, status, code, headers, data, cause);
            exceptionEntity.timestamp = this.timestamp;
            return exceptionEntity;
        }
    }
}
