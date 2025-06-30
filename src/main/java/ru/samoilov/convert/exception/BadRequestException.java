package ru.samoilov.convert.exception;

public class BadRequestException  extends  RuntimeException{
    public  BadRequestException(String message){
        super(message);
    }
}
