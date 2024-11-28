package org.dljl.controller;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AppointmentControllerIT {

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
      .andExpect(content().string("Recurring block created successfully from 2024-01-01 to 2024-12-31"));
  }

  @Test
  void testGetAvailableTimeIntervals() throws Exception {
    mockMvc.perform(get("/appointments/provider/1/available/date/2024-01-01"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2)) // Ensure at least one available interval exists
      .andExpect(jsonPath("$[0][0]").value("2024-01-01T00:00:00")) // Check the start time of the first interval
      .andExpect(jsonPath("$[0][1]").value("2024-01-01T09:00:00")); // Check the end time of the first interval
  }
  @Test
  void testGetAppointmentHistory() throws Exception {
    mockMvc.perform(get("/appointments/history")
        .param("provider_id", "1")
        .param("user_id", "2"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1)) // Ensure one appointment is returned
      .andExpect(jsonPath("$[0].['Appointment ID']").value(1)) // Validate the appointment ID
      .andExpect(jsonPath("$[0].['Status']").value("SCHEDULED")); // Check the status
  }
  @Test
  void testDeleteBlock() throws Exception {
    mockMvc.perform(delete("/appointments/deleteBlock/1"))
      .andExpect(status().isOk())
      .andExpect(content().string("Block cancelled successfully."));

    // Verify the block was deleted by checking it no longer exists
    mockMvc.perform(get("/appointments/provider/1/appointmentsByDate")
        .param("appointmentDate", "2024-01-01"))
      .andExpect(status().isNotFound());
  }
  @Test
  void testCancelNonExistentAppointment() throws Exception {
    mockMvc.perform(put("/appointments/cancel/999")) // Appointment ID 999 doesn't exist
      .andExpect(status().isBadRequest())
      .andExpect(content().string("Appointment not found or already cancelled."));
  }
  @Test
  void testGetAppointmentsWithInvalidDateRange() throws Exception {
    mockMvc.perform(get("/appointments/provider/1/appointments")
        .param("startDate", "2024-12-31")
        .param("endDate", "2024-01-01")) // End date before start date
      .andExpect(status().isBadRequest());
  }

  /**
   * Test getting appointments for a specific provider and date.
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

    // Validate cancellation in the database
    mockMvc.perform(get("/appointments/provider/1/appointments")
        .param("startDate", "2024-01-01")
        .param("endDate", "2024-12-31"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1)); // One appointment should be left
  }
//  @Test
//  void testUpdateNonExistentAppointment() throws Exception {
//    String updateJson = """
//            {
//                "appointmentId": 999,
//                "status": "COMPLETED",
//                "comments": "Updated comments for non-existent appointment"
//            }
//        """;
//
//    mockMvc.perform(put("/appointments/update")
//        .contentType(MediaType.APPLICATION_JSON)
//        .content(updateJson))
//      .andExpect(status().isBadRequest())
//      .andExpect(content().string("Appointment ID does not exist."));
//  }


  /**
   * Test handling of appointment conflicts.
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
      .andExpect(content().string("The selected time slot is not available or conflicts with an existing appointment."));
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

    // Verify the update in another request
    mockMvc.perform(get("/appointments/provider/1/appointments")
        .param("startDate", "2024-01-01")
        .param("endDate", "2024-12-31"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].status").value("COMPLETED"))
      .andExpect(jsonPath("$[0].comments").value("Updated comments"));
  }
}
