package app;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionhandlerController {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(code=HttpStatus.BAD_REQUEST)
    public String handleException(IllegalArgumentException e) {
        return e.getMessage();
    }
    
    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(code=HttpStatus.OK)
    public String handleInternalServerException(InternalServerException e) {
        return e.getMessage();
    }
    
}
