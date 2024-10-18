package org.dljl.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
   * Endpoint to register a new client. This endpoint generates a unique client ID using UUID and
   * returns it in the response. It also provides a message instructing the client to save the
   * generated ID.
   *
   * @return ResponseEntity containing a map with the generated client ID and an advisory message.
   *     The HTTP status code for the response is 201 (Created).
   */
  @PostMapping("/registerClient")
  public ResponseEntity<Map<String, String>> registerClient() {
    // Generate UUID for the client ID
    String clientId = UUID.randomUUID().toString();

    // Prepare response with client ID and message
    Map<String, String> response = new HashMap<>();
    response.put("client_id", clientId);
    response.put(
        "message",
        "SAVE YOUR CLIENT ID. "
            + "LOSING IT WILL POTENTIALLY LOSE PREVIOUS INFO AND REQUIRE YOU TO REGENERATE ONE");

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
