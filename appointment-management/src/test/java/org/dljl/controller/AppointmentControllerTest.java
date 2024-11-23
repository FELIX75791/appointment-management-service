package org.dljl.controller;

import org.dljl.dto.CreateAppointmentDto;
import org.dljl.dto.CreateRecurringBlockDto;
import org.dljl.entity.Appointment;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import org.dljl.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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

  @Test
  public void testGetAppointmentHistoryNoHistory() throws Exception {
    when(appointmentService.getAppointmentHistory(anyLong(), anyLong()))
      .thenReturn(Collections.emptyList());

    mockMvc.perform(get("/history")
        .param("provider_id", "1")
        .param("user_id", "2"))
      .andExpect(status().isNotFound());
  }
  @Test
  public void testGetAppointmentHistoryNoData() throws Exception {
    // Mock the service to return an empty list
    when(appointmentService.getAppointmentHistory(anyLong(), anyLong()))
      .thenReturn(Collections.emptyList());

    // Perform the GET request
    mockMvc.perform(get("/appointments/history")
        .param("provider_id", "1")
        .param("user_id", "2"))
      .andExpect(status().isOk());
  }


  @Test
  public void testGetAppointmentHistoryWithData() throws Exception {
    when(appointmentService.getAppointmentHistory(anyLong(), anyLong()))
      .thenReturn(List.of(createMockAppointment()));

    mockMvc.perform(get("/appointments/history")
        .param("provider_id", "1")
        .param("user_id", "2"))
      .andExpect(status().isOk());
  }

  @Test
  public void testCreateAppointmentWithValidInput() throws Exception {
    // JSON payload for the valid input
    String validAppointmentJson = """
            {
                "providerId": 1,
                "userId": 2,
                "startDateTime": "2024-01-01T09:00:00",
                "endDateTime": "2024-01-01T10:00:00",
                "status": "SCHEDULED",
                "serviceType": "Medical",
                "comments": "Valid appointment creation test"
            }
        """;

    // Mock Appointment object to simulate the service response
    Appointment mockAppointment = new Appointment();
    mockAppointment.setAppointmentId(1L);
    mockAppointment.setProviderId(1L);
    mockAppointment.setUserId(2L);
    mockAppointment.setStartDateTime(LocalDateTime.of(2024, 1, 1, 9, 0));
    mockAppointment.setEndDateTime(LocalDateTime.of(2024, 1, 1, 10, 0));
    mockAppointment.setStatus("SCHEDULED");
    mockAppointment.setServiceType("Medical");
    mockAppointment.setComments("Valid appointment creation test");

    // Mock the service layer
    when(appointmentService.createAppointment(any(CreateAppointmentDto.class)))
      .thenReturn(mockAppointment);

    // Perform the POST request
    mockMvc.perform(post("/appointments/createAppointment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(validAppointmentJson))
      .andExpect(status().isCreated()) // Verify HTTP 201 status
      .andExpect(jsonPath("$.appointmentId").value(1))
      .andExpect(jsonPath("$.providerId").value(1))
      .andExpect(jsonPath("$.status").value("SCHEDULED"))
      .andExpect(jsonPath("$.serviceType").value("Medical"))
      .andExpect(jsonPath("$.comments").value("Valid appointment creation test"));
  }





  @Test
  public void testGetAppointmentsWithinDateRange() throws Exception {
    // Mock data for the service
    Appointment mockAppointment = new Appointment();
    mockAppointment.setAppointmentId(1L);
    mockAppointment.setProviderId(1L);
    mockAppointment.setUserId(2L);
    mockAppointment.setStartDateTime(LocalDateTime.of(2024, 1, 1, 9, 0));
    mockAppointment.setEndDateTime(LocalDateTime.of(2024, 1, 1, 10, 0));
    mockAppointment.setStatus("Scheduled");
    mockAppointment.setServiceType("Medical");
    mockAppointment.setComments("Test appointment");

    // Stub the service method
    when(appointmentService.getAppointmentsWithinDateRange(anyLong(), any(), any()))
      .thenReturn(List.of(mockAppointment));

    // Perform the GET request
    mockMvc.perform(get("/appointments/provider/1/appointments")
        .param("startDate", "2024-01-01")
        .param("endDate", "2024-01-31"))
      .andExpect(status().isOk());
  }


  @Test
  public void testCreateRecurringBlockSuccess() throws Exception {
    String blockJson = """
            {
                "providerId": 1,
                "startDate": "2024-01-01",
                "endDate": "2024-01-10",
                "blockType": "Daily"
            }
        """;

    when(appointmentService.createRecurringBlock(any()))
      .thenReturn("Block created successfully.");

    mockMvc.perform(post("/appointments/createRecurringBlock")
        .contentType(MediaType.APPLICATION_JSON)
        .content(blockJson))
      .andExpect(status().isCreated())
      .andExpect(content().string("Block created successfully."));
  }

  @Test
  public void testDeleteNotExistBlock() throws Exception {
    when(appointmentService.deleteBlock(anyLong())).thenReturn(true);

    mockMvc.perform(delete("/deleteBlock/1"))
      .andExpect(status().isNotFound());
  }

  private Appointment createMockAppointment() {
    Appointment appointment = new Appointment();
    appointment.setAppointmentId(1L);
    appointment.setProviderId(1L);
    appointment.setUserId(2L);
    appointment.setStartDateTime(LocalDateTime.of(2024, 1, 1, 9, 0));
    appointment.setEndDateTime(LocalDateTime.of(2024, 1, 1, 10, 0));
    appointment.setStatus("Scheduled");
    appointment.setServiceType("Medical");
    appointment.setComments("Test appointment");
    return appointment;
  }

}
