package org.dljl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.dljl.dto.CreateAppointmentDto;
import org.dljl.entity.Appointment;
import org.dljl.mapper.AppointmentMapper;
import org.dljl.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** The type Appointment service test. */
public class AppointmentServiceTest {

  @Mock private AppointmentMapper appointmentMapper;

  @InjectMocks private AppointmentServiceImpl appointmentService;

  /** Sets up. */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this); // Initialize mocks
  }

  /** Test create appointment no conflicts success. */
  @Test
  void testCreateAppointment_noConflicts_success() {

    CreateAppointmentDto appointmentDTO = new CreateAppointmentDto();
    appointmentDTO.setProviderId(1L);
    appointmentDTO.setUserId(2L);
    appointmentDTO.setStartDateTime(LocalDateTime.of(2024, 10, 15, 10, 0));
    appointmentDTO.setEndDateTime(LocalDateTime.of(2024, 10, 15, 11, 0));
    appointmentDTO.setStatus("scheduled");
    appointmentDTO.setServiceType("consultation");
    appointmentDTO.setComments("Test appointment");

    when(appointmentMapper.checkCreateTimeConflict(anyLong(), any(), any())).thenReturn(0);

    Appointment result = appointmentService.createAppointment(appointmentDTO);

    assertNotNull(result);
    assertEquals(1L, result.getProviderId());
    assertEquals(2L, result.getUserId());
    assertEquals(LocalDateTime.of(2024, 10, 15, 10, 0), result.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 10, 15, 11, 0), result.getEndDateTime());

    verify(appointmentMapper).createAppointment(any(Appointment.class));
  }

  /** Test create appointment time conflict throws exception. */
  @Test
  void testCreateAppointment_timeConflict_throwsException() {

    CreateAppointmentDto appointmentDTO = new CreateAppointmentDto();
    appointmentDTO.setProviderId(1L);
    appointmentDTO.setStartDateTime(LocalDateTime.of(2024, 10, 15, 10, 0));
    appointmentDTO.setEndDateTime(LocalDateTime.of(2024, 10, 15, 11, 0));

    when(appointmentMapper.checkCreateTimeConflict(anyLong(), any(), any())).thenReturn(1);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> appointmentService.createAppointment(appointmentDTO));
    assertEquals(
        "The selected time slot is not available or conflicts with an existing appointment.",
        exception.getMessage());
  }

  /** Test get appointment history service layer. */
  @Test
  public void testGetAppointmentHistory_ServiceLayer() {
    // Creating mock data
    Appointment appointment1 = new Appointment();
    appointment1.setAppointmentId(1L);
    appointment1.setStartDateTime(LocalDateTime.now().minusDays(1));
    appointment1.setEndDateTime(LocalDateTime.now().minusDays(1).plusHours(1));
    appointment1.setStatus("completed");
    appointment1.setServiceType("Consultation");
    appointment1.setComments("First appointment.");

    Appointment appointment2 = new Appointment();
    appointment2.setAppointmentId(2L);
    appointment2.setStartDateTime(LocalDateTime.now().minusDays(2));
    appointment2.setEndDateTime(LocalDateTime.now().minusDays(2).plusHours(1));
    appointment2.setStatus("canceled");
    appointment2.setServiceType("Repair");
    appointment2.setComments("Client canceled.");

    // Mocking the service call to return a list of appointments
    List<Appointment> mockAppointments = Arrays.asList(appointment1, appointment2);
    when(appointmentService.getAppointmentHistory(1L, 1L)).thenReturn(mockAppointments);

    // Calling the service method
    List<Appointment> result = appointmentService.getAppointmentHistory(1L, 1L);

    // Validating the response
    assertEquals(2, result.size()); // Ensure two appointments are returned
    assertEquals("completed", result.get(0).getStatus());
    assertEquals("Consultation", result.get(0).getServiceType());
    assertEquals("First appointment.", result.get(0).getComments());
  }
}
