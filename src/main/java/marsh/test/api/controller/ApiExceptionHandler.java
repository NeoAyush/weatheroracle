package marsh.test.api.controller;

import marsh.test.api.domain.WeatherResponse;
import marsh.test.api.exceptions.InternalAPICallFailedException;
import marsh.test.api.exceptions.InvalidZipCodeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

@ControllerAdvice
public class ApiExceptionHandler {


    @ExceptionHandler(InvalidZipCodeException.class)
    public ResponseEntity<WeatherResponse> handleInvalidZipCodeException() {
        return new ResponseEntity(new WeatherResponse(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler({IOException.class, RestClientException.class})
    public ResponseEntity<WeatherResponse> handleIOException() {
        return new ResponseEntity(new WeatherResponse(), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(InternalAPICallFailedException.class)
    public ResponseEntity<WeatherResponse> handleInternalAPIFailedExcpetion() {
        return new ResponseEntity(new WeatherResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
