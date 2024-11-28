package org.dljl.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for AppointmentController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AppointmentControllerIt {

  @Autowired
  private MockMvc mockMvc;

  /**
   * Test getting appointments within a specific date range for a provider.
   */
  @Test
  void testGetAppointmentsWithinDateRange() throws Exception {
    mockMvc.perform(get("/appointments/provider/1/appointments")
        .param("startDate", "2024-01-01")
        .param("endDate", "2024-12-31"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].appointmentId").exists());
  }

  /**
   * Test creating a recurring block for a provider.
   */
  @Test
  void testCreateRecurringBlock() throws Exception {
    String blockDtoJson = """
                {
                    "providerId": 1,
                    "startTime": "10:00",
                    "endTime": "11:00",
                    "startDate": "2024-01-01",
                    "endDate": "2024-12-31"
                }
            """;

    mockMvc.perform(post("/appointments/createRecurringBlock")
        .contentType(MediaType.APPLICATION_JSON)
        .content(blockDtoJson))
        .andExpect(status().isCreated())
        .andExpect(content().string("Recurring block created successfully "
          + "from 2024-01-01 to 2024-12-31"));
  }

  @Test
  void testUpdateNonExistentAppointment() throws Exception {
    String updateJson = """
            {
                "appointmentId": 999,
                "status": "COMPLETED",
                "comments": "Updated comments for non-existent appointment"
            }
        """;

    mockMvc.perform(put("/appointments/update")
        .contentType(MediaType.APPLICATION_JSON)
        .content(updateJson))
      .andExpect(status().isBadRequest())
        .andExpect(content().string("Appointment ID does not exist."));
  }

  /**
   * Test getting available time intervals for a provider.
   */
  @Test
  void testGetAvailableTimeIntervals() throws Exception {
    mockMvc.perform(get("/appointments/provider/1/available/date/2024-01-01"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0][0]").value("2024-01-01T00:00:00"))
        .andExpect(jsonPath("$[0][1]").value("2024-01-01T09:00:00"));
  }

  /**
   * Test getting appointment history.
   */
  @Test
  void testGetAppointmentHistory() throws Exception {
    mockMvc.perform(get("/appointments/history")
        .param("provider_id", "1")
        .param("user_id", "2"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0]['Appointment ID']").value(1))
        .andExpect(jsonPath("$[0]['Status']").value("SCHEDULED"));
  }

  /**
   * Test deleting a block by ID.
   */
  @Test
  void testDeleteBlock() throws Exception {
    mockMvc.perform(delete("/appointments/deleteBlock/1"))
      .andExpect(status().isOk())
        .andExpect(content().string("Block cancelled successfully."));

    mockMvc.perform(get("/appointments/provider/1/appointmentsByDate")
        .param("appointmentDate", "2024-01-01"))
        .andExpect(status().isNotFound());
  }

  /**
   * Test cancelling a non-existent appointment.
   */
  @Test
  void testCancelNonExistentAppointment() throws Exception {
    mockMvc.perform(put("/appointments/cancel/999"))
      .andExpect(status().isBadRequest())
        .andExpect(content().string("Appointment not found or already cancelled."));
  }

  /**
   * Test handling invalid date range for appointments.
   */
  @Test
  void testGetAppointmentsWithInvalidDateRange() throws Exception {
    mockMvc.perform(get("/appointments/provider/1/appointments")
        .param("startDate", "2024-12-31")
        .param("endDate", "2024-01-01"))
        .andExpect(status().isBadRequest());
  }

  /**
   * Test getting appointments by provider and date.
   */
  @Test
  void testGetAppointmentsByProviderAndDate() throws Exception {
    mockMvc.perform(get("/appointments/provider/1/date/2024-01-01"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].status").value("SCHEDULED"));
  }

  /**
   * Test cancelling an appointment by ID.
   */
  @Test
  void testCancelAppointment() throws Exception {
    mockMvc.perform(put("/appointments/cancel/1"))
      .andExpect(status().isOk())
        .andExpect(content().string("Appointment cancelled successfully."));

    mockMvc.perform(get("/appointments/provider/1/appointments")
        .param("startDate", "2024-01-01")
        .param("endDate", "2024-12-31"))
      .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }

  /**
   * Test creating an appointment with a conflicting time slot.
   */
  @Test
  void testCreateAppointmentWithConflict() throws Exception {
    String conflictingAppointmentJson = """
                {
                    "providerId": 1,
                    "userId": 4,
                    "startDateTime": "2024-01-01T09:30:00",
                    "endDateTime": "2024-01-01T10:30:00",
                    "status": "SCHEDULED",
                    "serviceType": "Consultation",
                    "comments": "Conflict test appointment"
                }
            """;

    mockMvc.perform(post("/appointments/createAppointment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(conflictingAppointmentJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("The selected time slot is not available "
          + "or conflicts with an existing appointment."));
  }

  /**
   * Test updating an existing appointment.
   */
  @Test
  void testUpdateAppointment() throws Exception {
    String updateJson = """
                {
                    "appointmentId": 1,
                    "status": "COMPLETED",
                    "comments": "Updated comments"
                }
            """;

    mockMvc.perform(put("/appointments/update")
        .contentType(MediaType.APPLICATION_JSON)
        .content(updateJson))
        .andExpect(status().isOk());

    mockMvc.perform(get("/appointments/provider/1/appointments")
        .param("startDate", "2024-01-01")
        .param("endDate", "2024-12-31"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].status").value("COMPLETED"))
        .andExpect(jsonPath("$[0].comments").value("Updated comments"));
  }
}
