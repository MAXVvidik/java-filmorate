package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@Slf4j
public class EntityExceptionUser extends RuntimeException{
    @ExceptionHandler
    public ResponseEntity<String> handleIncorrectValidation(ValidationException e) {
        log.warn("При обработке запроса возникло исключение: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    @ResponseBody
    public ResponseEntity<String> handleException(Exception e) {
        log.warn("При обработке запроса возникло исключение " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler
    public ResponseEntity<String> handleNotFoundException(InputDataException e) {
        log.warn("При обработке запроса возникло исключение: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
