package ru.samoilov.convert.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.samoilov.convert.exception.*;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFountExchengeRateException.class)
    public ResponseEntity<ErrorResponse> conflictException(NotFountExchengeRateException e){
        return  buildResponse(HttpStatus.NOT_FOUND,"Обменный курс для пары не найден",e);
    }


    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> conflictException(ConflictException e){
        return  buildResponse(HttpStatus.CONFLICT,"Валюта с таким кодом уже существует",e);
    }



    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> sqlException(SQLException e){
        return  buildResponse(HttpStatus.NOT_FOUND,"sqlException",e);

    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> nullPointerException(NullPointerException e){
        return  buildResponse(HttpStatus.NOT_FOUND,"loh",e);

    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {;
        return  buildResponse(HttpStatus.NOT_FOUND,"Код валюты отсутствует в адресе",ex);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestEsception(BadRequestException e) {
        return buildResponse(HttpStatus.BAD_REQUEST,
                "Не корректный запрос",
                e);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND,
                "Данные не найдены ",
                e);
    }

    @ExceptionHandler(Exception.class) //Exception.class
    public ResponseEntity<ErrorResponse> exception500( Exception ex){
        return  buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,"ошибка сервера, ", ex);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, Throwable throwable) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.getReasonPhrase(),
                message,
                throwable.getMessage());

        return ResponseEntity.status(status).body(errorResponse);
    }

}
