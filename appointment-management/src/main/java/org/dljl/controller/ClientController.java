package org.dljl.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class that handles client registration and generates a unique client ID. Provides an
 * endpoint to register a client and return a unique client ID along with a message. Clients using
 * this service is responsible to save generated id.
 */
@RestController
@RequestMapping("clients")
public class ClientController {

  /**
   * Endpoint to register a new client. This endpoint generates a unique client ID using a
   * combination of the current timestamp and random numbers to avoid collisions. It returns the
   * client ID as a long and a message instructing the client to save the generated ID.
   *
   * @return ResponseEntity containing a map with the generated client ID and an advisory message.
   *     The HTTP status code for the response is 201 (Created).
   */
  @PostMapping("/registerClient")
  public ResponseEntity<Map<String, Object>> registerClient() {
    // Generate unique client ID using current timestamp and random number
    long currentTimeMillis = System.currentTimeMillis();
    long randomPart = ThreadLocalRandom.current().nextLong(1000, 9999);
    long clientId = currentTimeMillis * 10000 + randomPart;

    // Prepare response with client ID and message
    Map<String, Object> response = new HashMap<>();
    response.put("client_id", clientId);
    response.put(
        "message",
        "SAVE YOUR CLIENT ID. "
            + "LOSING IT WILL POTENTIALLY LOSE PREVIOUS INFO AND REQUIRE YOU TO REGENERATE ONE");

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
