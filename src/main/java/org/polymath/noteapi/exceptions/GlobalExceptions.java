package org.polymath.noteapi.exceptions;

import org.polymath.noteapi.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptions {

    @ExceptionHandler(UserDoesNotExist.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse userDoesNotExist(final UserDoesNotExist ex) {
        return new ErrorResponse(ex.getMessage(),HttpStatus.NOT_FOUND,System.currentTimeMillis());
    }

    @ExceptionHandler(CustomBadRequest.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCustomBadRequest(CustomBadRequest ex){
        return new ErrorResponse(ex.getMessage(),HttpStatus.BAD_REQUEST,System.currentTimeMillis());
    }

    @ExceptionHandler(CustomNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCustomNotFound(CustomNotFound ex){
        return new ErrorResponse(ex.getMessage(),HttpStatus.NOT_FOUND,System.currentTimeMillis());
    }

}
