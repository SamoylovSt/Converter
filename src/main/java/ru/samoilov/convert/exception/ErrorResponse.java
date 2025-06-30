package ru.samoilov.convert.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String status;

    private String devMessage;

    private String userMessage;
}
