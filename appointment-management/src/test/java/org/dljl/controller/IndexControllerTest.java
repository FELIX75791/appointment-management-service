package org.dljl.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Index Controller Test.
 */
@WebMvcTest(IndexController.class)
public class IndexControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testIndexRoute() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().isOk()) // Verify HTTP 200 status
        .andExpect(content().string("Welcome to the Appointment Management Service!"));
  }
}
