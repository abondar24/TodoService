package org.abondar.industrial.todo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class ItemExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ItemNotFoundException.class)
  public void handleNotFound(Exception ex, HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
  }

  @ExceptionHandler(ItemChangeException.class)
  public void handleBadRequest(Exception ex, HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
  }
}
