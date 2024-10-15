package org.dljl.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * IndexController is the main controller that defines the root route ("/"). This controller is used
 * to test the application and provide a welcome message.
 *
 * <p>It handles HTTP GET requests to the root URL and returns a simple welcome message as a string
 * response.
 *
 * <p>Annotated with {@code @RestController}, this class handles RESTful web service requests.
 */
@RestController
public class IndexController {

  /**
   * Index string.
   *
   * @return the string
   */
  // Define the root route to test the application
  @GetMapping("/")
  public String index() {
    return "Welcome to the Appointment Management Service!";
  }
}
