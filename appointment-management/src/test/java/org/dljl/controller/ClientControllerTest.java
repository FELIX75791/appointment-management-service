package org.dljl.controller;

import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

@WebMvcTest(ClientController.class)
public class ClientControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void testRegisterClient() throws Exception {
    mockMvc.perform(post("/clients/registerClient"))
      .andExpect(status().isCreated()) // Assert HTTP 201
      .andExpect(jsonPath("$.client_id").exists()) // Check client_id exists
      .andExpect(jsonPath("$.client_id").isNumber()) // Validate it's a number
      .andExpect(jsonPath("$.message").value(
        "SAVE YOUR CLIENT ID. LOSING IT WILL POTENTIALLY LOSE PREVIOUS INFO AND REQUIRE YOU TO REGENERATE ONE")); // Assert message
  }
}
