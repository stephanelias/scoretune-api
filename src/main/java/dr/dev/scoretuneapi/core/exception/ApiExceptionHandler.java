package dr.dev.scoretuneapi.core.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({ApiException.class})
    public ResponseEntity<ExceptionEntity> handleApiException(ApiException e, WebRequest request) {
        ExceptionEntity exceptionEntity = new ExceptionEntity.Builder(e.getExceptionEntity())
                .cause(e)
                .build();
        return exceptionEntity.toResponseEntity(request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            String fieldPath = violation.getPropertyPath().toString();
            String field = fieldPath.contains(".") ?
                    fieldPath.substring(fieldPath.lastIndexOf('.') + 1) :
                    fieldPath;

            errors.put(field, violation.getMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

}
