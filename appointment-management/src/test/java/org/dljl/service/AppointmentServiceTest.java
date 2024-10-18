package org.dljl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import org.dljl.dto.CreateAppointmentDto;
import org.dljl.dto.CreateBlockDto;
import org.dljl.dto.UpdateAppointmentDto;
import org.dljl.entity.Appointment;
import org.dljl.mapper.AppointmentMapper;
import org.dljl.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * The type Appointment service test.
 */
public class AppointmentServiceTest {

  @Mock
  private AppointmentMapper appointmentMapper;

  @InjectMocks
  private AppointmentServiceImpl appointmentService;

  /**
   * Sets up.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this); // Initialize mocks
  }

  /**
   * Test create appointment no conflicts success.
   */
  @Test
  void testCreateAppointment_noConflicts_success() {

    CreateAppointmentDto appointmentDto = new CreateAppointmentDto();
    appointmentDto.setProviderId(1L);
    appointmentDto.setUserId(2L);
    appointmentDto.setStartDateTime(LocalDateTime.of(2024, 10, 15, 10, 0));
    appointmentDto.setEndDateTime(LocalDateTime.of(2024, 10, 15, 11, 0));
    appointmentDto.setStatus("scheduled");
    appointmentDto.setServiceType("consultation");
    appointmentDto.setComments("Test appointment");

    when(appointmentMapper.checkCreateTimeConflict(anyLong(), any(), any())).thenReturn(0);

    Appointment result = appointmentService.createAppointment(appointmentDto);

    assertNotNull(result);
    assertEquals(1L, result.getProviderId());
    assertEquals(2L, result.getUserId());
    assertEquals(LocalDateTime.of(2024, 10, 15, 10, 0), result.getStartDateTime());
    assertEquals(LocalDateTime.of(2024, 10, 15, 11, 0), result.getEndDateTime());

    verify(appointmentMapper).createAppointment(any(Appointment.class));
  }

  /**
   * Test create appointment time conflict throws exception.
   */
  @Test
  void testCreateAppointment_timeConflict_throwsException() {

    CreateAppointmentDto appointmentDto = new CreateAppointmentDto();
    appointmentDto.setProviderId(1L);
    appointmentDto.setStartDateTime(LocalDateTime.of(2024, 10, 15, 10, 0));
    appointmentDto.setEndDateTime(LocalDateTime.of(2024, 10, 15, 11, 0));

    when(appointmentMapper.checkCreateTimeConflict(anyLong(), any(), any())).thenReturn(1);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> appointmentService.createAppointment(appointmentDto));
    assertEquals(
        "The selected time slot is not available or conflicts with an existing appointment.",
        exception.getMessage());
  }

  @Test
  void testCreateAppointment_throwsIllegalArgumentException() {
    CreateAppointmentDto appointmentDto = new CreateAppointmentDto();
    appointmentDto.setProviderId(1L);
    appointmentDto.setStartDateTime(LocalDateTime.of(2024, 10, 15, 10, 0));
    appointmentDto.setEndDateTime(LocalDateTime.of(2024, 10, 15, 11, 0));

    when(appointmentMapper.checkCreateTimeConflict(anyLong(), any(), any())).thenReturn(1);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      appointmentService.createAppointment(appointmentDto);
    });

    assertEquals(
        "The selected time slot is not available or conflicts with an existing appointment.",
        exception.getMessage());
  }

  @Test
  void testCreateAppointment_throwsGenericException() {
    CreateAppointmentDto appointmentDto = new CreateAppointmentDto();
    appointmentDto.setProviderId(1L);
    appointmentDto.setStartDateTime(LocalDateTime.of(2024, 10, 15, 10, 0));
    appointmentDto.setEndDateTime(LocalDateTime.of(2024, 10, 15, 11, 0));

    when(appointmentMapper.checkCreateTimeConflict(anyLong(), any(), any()))
        .thenThrow(new RuntimeException("System Error"));

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      appointmentService.createAppointment(appointmentDto);
    });

    assertEquals("System Error", exception.getMessage());
  }


  /**
   * Test get appointment history service layer.
   */
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

  @Test
  void testCreateBlock_success() {
    CreateBlockDto blockDto = new CreateBlockDto();
    blockDto.setProviderId(1L);
    blockDto.setStartDateTime(LocalDateTime.of(2024, 10, 15, 8, 0));
    blockDto.setEndDateTime(LocalDateTime.of(2024, 10, 15, 9, 0));

    when(appointmentMapper.checkCreateTimeConflict(anyLong(), any(), any())).thenReturn(0);

    String result = appointmentService.createBlock(blockDto);

    assertEquals("Block Created Successfully", result);
    verify(appointmentMapper).createAppointment(any(Appointment.class));
  }

  @Test
  void testCreateBlock_conflict_throwsException() {
    CreateBlockDto blockDto = new CreateBlockDto();
    blockDto.setProviderId(1L);
    blockDto.setStartDateTime(LocalDateTime.of(2024, 10, 15, 8, 0));
    blockDto.setEndDateTime(LocalDateTime.of(2024, 10, 15, 9, 0));

    when(appointmentMapper.checkCreateTimeConflict(anyLong(), any(), any())).thenReturn(1);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> appointmentService.createBlock(blockDto)
    );

    assertEquals("The selected time slot is not available or conflicts "
        + "with an existing appointment. To block this time, please cancel "
        + "the conflicting appointment or block.", exception.getMessage());
  }

  @Test
  void testCreateBlock_throwsIllegalArgumentException_providerIdNull() {
    CreateBlockDto blockDto = new CreateBlockDto();
    blockDto.setStartDateTime(LocalDateTime.of(2024, 10, 15, 8, 0));
    blockDto.setEndDateTime(LocalDateTime.of(2024, 10, 15, 9, 0));
    blockDto.setProviderId(null);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      appointmentService.createBlock(blockDto);
    });

    assertEquals("Provider ID Can't be null.", exception.getMessage());
  }


  @Test
  void testUpdateAppointment_success() {
    UpdateAppointmentDto updateDto = new UpdateAppointmentDto();
    updateDto.setAppointmentId(1L);
    updateDto.setStartDateTime(LocalDateTime.of(2024, 10, 16, 10, 0));
    updateDto.setEndDateTime(LocalDateTime.of(2024, 10, 16, 11, 0));

    when(appointmentMapper.checkUpdateTimeConflict(anyLong(), any(), any())).thenReturn(0);
    when(appointmentMapper.getAppointment(anyLong())).thenReturn(new Appointment());

    Appointment updatedAppointment = appointmentService.updateAppointment(updateDto);

    assertNotNull(updatedAppointment);
    verify(appointmentMapper).updateAppointment(updateDto);
  }

  @Test
  void testUpdateAppointment_AppointmentIdNull() {
    UpdateAppointmentDto updateDto = new UpdateAppointmentDto();
    updateDto.setStartDateTime(LocalDateTime.of(2024, 10, 15, 10, 0));
    updateDto.setEndDateTime(LocalDateTime.of(2024, 10, 15, 11, 0));
    updateDto.setAppointmentId(null);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      appointmentService.updateAppointment(updateDto);
    });

    assertEquals("Appointment ID is required for updating an appointment.",
        exception.getMessage());
  }


  @Test
  void testUpdateAppointment_conflict_throwsException() {
    UpdateAppointmentDto updateDto = new UpdateAppointmentDto();
    updateDto.setAppointmentId(1L);
    updateDto.setStartDateTime(LocalDateTime.of(2024, 10, 16, 10, 0));
    updateDto.setEndDateTime(LocalDateTime.of(2024, 10, 16, 11, 0));

    when(appointmentMapper.checkUpdateTimeConflict(anyLong(), any(), any())).thenReturn(1);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> appointmentService.updateAppointment(updateDto)
    );

    assertEquals("The selected time slot conflicts with an existing appointment.",
        exception.getMessage());
  }

  @Test
  void testUpdateAppointment_throwsIllegalArgumentException() {
    UpdateAppointmentDto updateDto = new UpdateAppointmentDto();
    updateDto.setAppointmentId(1L);
    updateDto.setStartDateTime(LocalDateTime.of(2024, 10, 15, 10, 0));
    updateDto.setEndDateTime(LocalDateTime.of(2024, 10, 15, 11, 0));

    when(appointmentMapper.checkUpdateTimeConflict(anyLong(), any(), any())).thenReturn(1);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      appointmentService.updateAppointment(updateDto);
    });

    assertEquals(
        "The selected time slot conflicts with an existing appointment.",
        exception.getMessage());
  }


  @Test
  void testCancelAppointment_success() {
    when(appointmentMapper.cancelAppointment(anyLong())).thenReturn(1);

    boolean result = appointmentService.cancelAppointment(1L);

    assertTrue(result);
    verify(appointmentMapper).cancelAppointment(1L);
  }

  @Test
  void testCancelAppointment_failure() {
    when(appointmentMapper.cancelAppointment(anyLong())).thenReturn(0);

    boolean result = appointmentService.cancelAppointment(1L);

    assertFalse(result);
    verify(appointmentMapper).cancelAppointment(1L);
  }

  @Test
  void testGetAvailableTimeIntervals_noAppointments_returnsFullDay() {
    LocalDate date = LocalDate.of(2024, 10, 15);
    when(appointmentMapper.getAppointmentsByProviderAndDate(anyLong(), any()))
        .thenReturn(Arrays.asList());

    List<List<LocalDateTime>> result = appointmentService.getAvailableTimeIntervals(1L, date);

    assertEquals(1, result.size());
    assertEquals(date.atStartOfDay(), result.get(0).get(0));
    assertEquals(date.atTime(LocalTime.MAX), result.get(0).get(1));
  }

  @Test
  void testGetAvailableTimeIntervals_withAppointments_returnsIntervals() {
    Appointment appointment1 = new Appointment();
    appointment1.setStartDateTime(LocalDateTime.of(2024, 10, 15, 10, 0));
    appointment1.setEndDateTime(LocalDateTime.of(2024, 10, 15, 11, 0));

    Appointment appointment2 = new Appointment();
    appointment2.setStartDateTime(LocalDateTime.of(2024, 10, 15, 13, 0));
    appointment2.setEndDateTime(LocalDateTime.of(2024, 10, 15, 14, 0));

    when(appointmentMapper.getAppointmentsByProviderAndDate(anyLong(), any()))
        .thenReturn(Arrays.asList(appointment1, appointment2));

    LocalDate date = LocalDate.of(2024, 10, 15);

    List<List<LocalDateTime>> result = appointmentService.getAvailableTimeIntervals(1L, date);

    assertEquals(3, result.size());
    assertEquals(LocalDateTime.of(2024, 10, 15, 0, 0), result.get(0).get(0));
    assertEquals(LocalDateTime.of(2024, 10, 15, 10, 0), result.get(0).get(1));
    assertEquals(LocalDateTime.of(2024, 10, 15, 11, 0), result.get(1).get(0));
    assertEquals(LocalDateTime.of(2024, 10, 15, 13, 0), result.get(1).get(1));
  }

  @Test
  void testGetAvailableTimeIntervals_withNoAppointments() {
    LocalDate date = LocalDate.of(2024, 10, 15);
    when(appointmentMapper.getAppointmentsByProviderAndDate(anyLong(), any()))
        .thenReturn(Arrays.asList());

    List<List<LocalDateTime>> result = appointmentService.getAvailableTimeIntervals(1L, date);

    assertEquals(1, result.size());
    assertEquals(date.atStartOfDay(), result.get(0).get(0));
    assertEquals(date.atTime(LocalTime.MAX), result.get(0).get(1));
  }

  @Test
  void testGetAvailableTimeIntervals_withMultipleAppointments() {
    Appointment appointment1 = new Appointment();
    appointment1.setStartDateTime(LocalDateTime.of(2024, 10, 15, 10, 0));
    appointment1.setEndDateTime(LocalDateTime.of(2024, 10, 15, 11, 0));

    Appointment appointment2 = new Appointment();
    appointment2.setStartDateTime(LocalDateTime.of(2024, 10, 15, 14, 0));
    appointment2.setEndDateTime(LocalDateTime.of(2024, 10, 15, 15, 0));

    when(appointmentMapper.getAppointmentsByProviderAndDate(anyLong(), any()))
        .thenReturn(Arrays.asList(appointment1, appointment2));

    LocalDate date = LocalDate.of(2024, 10, 15);

    List<List<LocalDateTime>> result = appointmentService.getAvailableTimeIntervals(1L, date);

    assertEquals(3, result.size()); // 确保三个可用时间段
    assertEquals(LocalDateTime.of(2024, 10, 15, 0, 0), result.get(0).get(0));
    assertEquals(LocalDateTime.of(2024, 10, 15, 10, 0), result.get(0).get(1));
    assertEquals(LocalDateTime.of(2024, 10, 15, 11, 0), result.get(1).get(0));
    assertEquals(LocalDateTime.of(2024, 10, 15, 14, 0), result.get(1).get(1));
  }

  @Test
  void testGetAvailableTimeIntervals_checkCurrentStart() {
    Appointment appointment1 = new Appointment();
    appointment1.setStartDateTime(LocalDateTime.of(2024, 10, 15, 10, 0));
    appointment1.setEndDateTime(LocalDateTime.of(2024, 10, 15, 11, 0));

    Appointment appointment2 = new Appointment();
    appointment2.setStartDateTime(LocalDateTime.of(2024, 10, 15, 14, 0));
    appointment2.setEndDateTime(LocalDateTime.of(2024, 10, 15, 15, 0));

    when(appointmentMapper.getAppointmentsByProviderAndDate(anyLong(), any()))
        .thenReturn(Arrays.asList(appointment1, appointment2));

    LocalDate date = LocalDate.of(2024, 10, 15);

    List<List<LocalDateTime>> result = appointmentService.getAvailableTimeIntervals(1L, date);

    assertEquals(3, result.size());

    assertEquals(LocalDateTime.of(2024, 10, 15, 0, 0), result.get(0).get(0));
    assertEquals(LocalDateTime.of(2024, 10, 15, 10, 0), result.get(0).get(1));

    assertEquals(LocalDateTime.of(2024, 10, 15, 11, 0), result.get(1).get(0));
    assertEquals(LocalDateTime.of(2024, 10, 15, 14, 0), result.get(1).get(1));

    assertEquals(LocalDateTime.of(2024, 10, 15, 15, 0), result.get(2).get(0));
    assertEquals(LocalDateTime.of(2024, 10, 15, 23, 59, 59, 999999999), result.get(2).get(1));
  }

  @Test
  void testUpdateAppointmentDtoSetAndGetUserId() {
    UpdateAppointmentDto dto = new UpdateAppointmentDto();
    dto.setUserId(100L);
    assertEquals(100L, dto.getUserId());
  }

  @Test
  void testUpdateAppointmentDtoSetAndGetStatus() {
    UpdateAppointmentDto dto = new UpdateAppointmentDto();
    dto.setStatus("confirmed");
    assertEquals("confirmed", dto.getStatus());
  }

  @Test
  void testUpdateAppointmentDtoSetAndGetServiceType() {
    UpdateAppointmentDto dto = new UpdateAppointmentDto();
    dto.setServiceType("consultation");
    assertEquals("consultation", dto.getServiceType());
  }

  @Test
  void testUpdateAppointmentDtoSetAndGetComments() {
    UpdateAppointmentDto dto = new UpdateAppointmentDto();
    dto.setComments("test comment.");
    assertEquals("test comment.", dto.getComments());
  }

  @Test
  void testUpdateAppointmentEntityGetAppointmentId() {
    Appointment appointment = new Appointment();
    appointment.setAppointmentId(10L);
    assertEquals(10L, appointment.getAppointmentId());
  }

  @Test
  void testGetAppointmentsByProviderId_success() {
    Appointment appointment1 = new Appointment();
    appointment1.setProviderId(1L);
    appointment1.setUserId(2L);
    appointment1.setStartDateTime(LocalDateTime.of(2024, 10, 15, 10, 0));
    appointment1.setEndDateTime(LocalDateTime.of(2024, 10, 15, 11, 0));

    Appointment appointment2 = new Appointment();
    appointment2.setProviderId(1L);
    appointment2.setUserId(3L);
    appointment2.setStartDateTime(LocalDateTime.of(2024, 10, 16, 14, 0));
    appointment2.setEndDateTime(LocalDateTime.of(2024, 10, 16, 15, 0));

    when(appointmentMapper.getAppointmentsByProviderId(1L)).thenReturn(
        Arrays.asList(appointment1, appointment2));

    List<Appointment> result = appointmentService.getAppointmentsByProviderId(1L);

    assertEquals(2, result.size());
    assertEquals(2L, result.get(0).getUserId());
    assertEquals(3L, result.get(1).getUserId());
    verify(appointmentMapper).getAppointmentsByProviderId(1L);
  }

  @Test
  void testGetAppointment_success() {
    Appointment appointment = new Appointment();
    appointment.setAppointmentId(1L);
    appointment.setProviderId(1L);
    appointment.setUserId(2L);
    appointment.setStartDateTime(LocalDateTime.of(2024, 10, 15, 10, 0));
    appointment.setEndDateTime(LocalDateTime.of(2024, 10, 15, 11, 0));

    when(appointmentMapper.getAppointment(1L)).thenReturn(appointment);

    Appointment result = appointmentService.getAppointment(1L);

    assertNotNull(result);
    assertEquals(1L, result.getAppointmentId());
    assertEquals(1L, result.getProviderId());
    assertEquals(2L, result.getUserId());
    verify(appointmentMapper).getAppointment(1L);
  }

  @Test
  void testGetAppointmentsByProviderAndDate_success() {

    Appointment appointment1 = new Appointment();
    appointment1.setProviderId(1L);
    appointment1.setUserId(2L);
    appointment1.setStartDateTime(LocalDateTime.of(2024, 10, 15, 10, 0));
    appointment1.setEndDateTime(LocalDateTime.of(2024, 10, 15, 11, 0));

    Appointment appointment2 = new Appointment();
    appointment2.setProviderId(1L);
    appointment2.setUserId(3L);
    appointment2.setStartDateTime(LocalDateTime.of(2024, 10, 15, 14, 0));
    appointment2.setEndDateTime(LocalDateTime.of(2024, 10, 15, 15, 0));

    LocalDate appointmentDate = LocalDate.of(2024, 10, 15);

    when(appointmentMapper.getAppointmentsByProviderAndDate(1L, appointmentDate)).thenReturn(
        Arrays.asList(appointment1, appointment2));

    List<Appointment> result = appointmentService.getAppointmentsByProviderAndDate(1L,
        appointmentDate);

    assertEquals(2, result.size());
    assertEquals(2L, result.get(0).getUserId());
    assertEquals(3L, result.get(1).getUserId());
    verify(appointmentMapper).getAppointmentsByProviderAndDate(1L, appointmentDate);
  }

}