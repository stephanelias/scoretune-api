package dr.dev.scoretuneapi.core.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({ApiException.class})
    public ResponseEntity<ExceptionEntity> handleApiException(ApiException e, WebRequest request) {
        ExceptionEntity exceptionEntity = new ExceptionEntity.Builder(e.getExceptionEntity())
                .cause(e)
                .build();
        return exceptionEntity.toResponseEntity(request);
    }

}
