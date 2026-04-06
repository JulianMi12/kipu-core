package com.kipu.core.common.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.core.env.ConfigurableEnvironment;

public class KipuBannerInitializer implements EnvironmentPostProcessor {

  private static final DeferredLog log = new DeferredLog();

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    // Colores para la consola
    String cyan = "\u001B[36m";
    String brightCyan = "\u001B[96m";
    String reset = "\u001B[0m";

    try {
      System.out.println(brightCyan + "\n>> [KIPU SYSTEM CHECK]" + reset);

      String[] components = {"NETWORK", "SECURITY", "DATABASE", "IDENTITY"};

      for (String comp : components) {
        System.out.print(cyan + "  Optimizing " + comp + " modules... " + reset);
        Thread.sleep(120);
        System.out.print(brightCyan + "[DONE]\n" + reset);
      }

      System.out.println(brightCyan + ">> ALL ENGINES READY. DEPLOYING CORE...\n" + reset);
      Thread.sleep(200);

    } catch (InterruptedException e) {
      log.error("Kipu boot sequence interrupted", e);
      Thread.currentThread().interrupt();
    }
  }
}
