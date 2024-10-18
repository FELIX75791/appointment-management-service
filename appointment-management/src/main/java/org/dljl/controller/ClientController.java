package org.dljl.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("clients")
public class ClientController {

  @PostMapping("/registerClient")
  public ResponseEntity<Map<String, String>> registerClient() {
    // Generate UUID for the client ID
    String clientId = UUID.randomUUID().toString();

    // Prepare response with client ID and message
    Map<String, String> response = new HashMap<>();
    response.put("client_id", clientId);
    response.put("message", "SAVE YOUR CLIENT ID. LOSING IT WILL POTENTIALLY LOSE PREVIOUS INFO AND REQUIRE YOU TO REGENERATE ONE");

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}