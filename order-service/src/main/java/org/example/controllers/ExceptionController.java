package org.example.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.example.model.InvalidParametersException;
import org.example.model.NotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ExceptionController {

    @ExceptionHandler(InvalidParametersException.class)
    void handleInvalidParameters(HttpServletResponse response, Exception exception) throws IOException {
        setResponse(response, HttpServletResponse.SC_BAD_REQUEST, exception);
    }

    @ExceptionHandler(NotFoundException.class)
    void handleNotFound(HttpServletResponse response, Exception exception) throws IOException {
        setResponse(response, HttpServletResponse.SC_NOT_FOUND, exception);
    }

    private void setResponse(HttpServletResponse response, int status, Exception exception) throws IOException {
        response.setStatus(status);
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()))) {
            bw.write(exception.getMessage());
        }
    }
}