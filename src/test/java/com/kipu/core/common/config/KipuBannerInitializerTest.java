package com.kipu.core.common.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

class KipuBannerInitializerTest {

  private final KipuBannerInitializer initializer = new KipuBannerInitializer();

  @Test
  @DisplayName("postProcessEnvironment: Should execute boot sequence without exceptions")
  void postProcessEnvironment_ShouldExecuteSequenceSuccessfully() {
    // Arrange
    ConfigurableEnvironment environment = mock(ConfigurableEnvironment.class);
    SpringApplication application = mock(SpringApplication.class);

    // Act & Assert
    assertDoesNotThrow(() -> initializer.postProcessEnvironment(environment, application));
  }

  @Test
  @DisplayName(
      "postProcessEnvironment: Should handle InterruptedException and restore interrupt status")
  void postProcessEnvironment_ShouldHandleInterruption() {
    // Arrange
    ConfigurableEnvironment environment = mock(ConfigurableEnvironment.class);
    SpringApplication application = mock(SpringApplication.class);

    // Forzamos la interrupción del hilo actual antes de ejecutar
    Thread.currentThread().interrupt();

    // Act
    initializer.postProcessEnvironment(environment, application);

    // Assert
    // Verificamos que el flag de interrupción se haya restaurado en el catch
    assertTrue(Thread.interrupted());
  }
}
