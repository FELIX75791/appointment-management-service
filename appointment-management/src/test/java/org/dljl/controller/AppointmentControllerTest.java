package org.dljl.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dljl.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Appointment Controller tests.
 */
public class AppointmentControllerTest {

  private MockMvc mockMvc;

  @Mock
  private AppointmentService appointmentService;

  @InjectMocks
  private AppointmentController appointmentController;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();
  }

  @Test
  public void testCreateAppointment() throws Exception {
    String appointmentJson = """
            {
                "providerId": 1,
                "userId": 2,
                "startDateTime": "2024-01-01T09:00:00",
                "endDateTime": "2024-01-01T10:00:00",
                "status": "SCHEDULED",
                "serviceType": "Medical",
                "comments": "Test comments"
            }
        """;

    when(appointmentService.createAppointment(any())).thenReturn(null);

    mockMvc.perform(post("/appointments/createAppointment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(appointmentJson))
        .andExpect(status().isCreated());
  }

  @Test
  public void testCreateBlock() throws Exception {
    String blockJson = """
            {
                "providerId": 1,
                "startDateTime": "2024-01-01T09:00:00",
                "endDateTime": "2024-01-01T10:00:00"
            }
        """;

    when(appointmentService.createBlock(any())).thenReturn("Block created");

    mockMvc.perform(post("/appointments/createBlock")
            .contentType(MediaType.APPLICATION_JSON)
            .content(blockJson))
        .andExpect(status().isCreated())
        .andExpect(content().string("Block created"));
  }

  @Test
  public void testCreateRecurringBlockInOneYear() throws Exception {
    String recurringBlockJson = """
            {
                "providerId": 1,
                "startTime": "09:00:00",
                "endTime": "10:00:00"
            }
        """;

    when(appointmentService.createRecurringBlockInOneYear(any())).thenReturn(
        "Recurring block created");

    mockMvc.perform(post("/appointments/createRecurringBlockInOneYear")
            .contentType(MediaType.APPLICATION_JSON)
            .content(recurringBlockJson))
        .andExpect(status().isCreated())
        .andExpect(content().string("Recurring block created"));
  }

  @Test
  public void testUpdateAppointment() throws Exception {
    String updateAppointmentJson = """
            {
                "appointmentId": 1,
                "userId": 2,
                "startDateTime": "2024-01-01T09:00:00",
                "endDateTime": "2024-01-01T10:00:00",
                "status": "RESCHEDULED",
                "serviceType": "Medical",
                "comments": "Updated comments"
            }
        """;

    mockMvc.perform(put("/appointments/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateAppointmentJson))
        .andExpect(status().isOk());
  }

  @Test
  public void testCancelAppointment() throws Exception {
    when(appointmentService.cancelAppointment(anyLong())).thenReturn(true);

    mockMvc.perform(put("/appointments/cancel/1"))
        .andExpect(status().isOk())
        .andExpect(content().string("Appointment cancelled successfully."));
  }

  @Test
  public void testGetAppointmentsByProviderId() throws Exception {
    mockMvc.perform(get("/appointments/provider/1"))
        .andExpect(status().isOk());
  }

  @Test
  public void testGetAppointmentsByProviderAndDate() throws Exception {
    mockMvc.perform(get("/appointments/provider/1/date/2024-01-01"))
        .andExpect(status().isOk());
  }

  @Test
  public void testGetAvailableTimeIntervals() throws Exception {
    mockMvc.perform(get("/appointments/provider/1/available/date/2024-01-01"))
        .andExpect(status().isOk());
  }

  @Test
  public void testCreateAppointmentWithInvalidInput() throws Exception {
    // Prepare the invalid input
    String invalidAppointmentJson = """
            {
                "providerId": null,
                "userId": 2,
                "startDateTime": "2024-01-01T09:00:00",
                "endDateTime": "2024-01-01T10:00:00",
                "status": "SCHEDULED",
                "serviceType": "Medical",
                "comments": "Test comments"
            }
        """;

    // Mock the service to throw an IllegalArgumentException for invalid input
    when(appointmentService.createAppointment(any()))
        .thenThrow(new IllegalArgumentException("Provider ID cannot be null"));

    // Perform the POST request and expect a 400 Bad Request status
    mockMvc.perform(post("/appointments/createAppointment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidAppointmentJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Provider ID cannot be null"));
  }
}
