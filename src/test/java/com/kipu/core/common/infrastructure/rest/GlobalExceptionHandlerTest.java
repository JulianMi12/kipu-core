package com.kipu.core.common.infrastructure.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.kipu.core.common.domain.exception.BusinessException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  @Test
  void handleBusinessException_ShouldReturnProblemDetailWithCorrectStatus() {
    // Arrange
    String errorMessage = "User already exists";
    HttpStatus status = HttpStatus.CONFLICT;

    BusinessException ex = new BusinessException(errorMessage, status) {};

    // Act
    ProblemDetail result = handler.handleBusinessException(ex);

    // Assert
    assertThat(result.getStatus()).isEqualTo(status.value());
    assertThat(result.getDetail()).isEqualTo(errorMessage);
    assertThat(result.getTitle()).isEqualTo("Business Rule Violation");
  }

  @Test
  @SuppressWarnings("unchecked")
  void handleValidationErrors_ShouldReturnListWithFieldErrors() {
    // Arrange
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);

    FieldError fieldError = new FieldError("user", "email", "must be a valid email");

    when(ex.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

    // Act
    ProblemDetail result = handler.handleValidationErrors(ex);

    // Assert
    assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(result.getTitle()).isEqualTo("Validation Error");

    List<String> errors = (List<String>) result.getProperties().get("errors");
    assertThat(errors).contains("email: must be a valid email");
  }

  @Test
  void handleGenericError_ShouldReturnInternalServerError() {
    // Arrange
    Exception ex = new RuntimeException("Database down");

    // Act
    ProblemDetail result = handler.handleGenericError(ex);

    // Assert
    assertThat(result.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(result.getDetail()).isEqualTo("An unexpected error occurred.");
    assertThat(result.getTitle()).isEqualTo("Internal Server Error");
  }
}
