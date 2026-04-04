package com.kipu.core.common.infrastructure.rest;

import com.kipu.core.common.domain.exception.BusinessException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ProblemDetail handleBusinessException(BusinessException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(ex.getHttpStatus(), ex.getMessage());
    problem.setTitle("Business Rule Violation");
    return problem;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
    List<String> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .toList();

    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request had invalid fields.");
    problem.setTitle("Validation Error");
    problem.setProperty("errors", errors);
    return problem;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGenericError(Exception ex) {
    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    problem.setTitle("Internal Server Error");
    return problem;
  }
}
