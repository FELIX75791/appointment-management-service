package org.dljl.controller;

import java.util.List;
import org.dljl.dto.CreateAppointmentDTO;
import org.dljl.dto.UpdateAppointmentDTO;
import org.dljl.entity.Appointment;
import org.dljl.entity.Availability;
import org.dljl.entity.Availability.Day;
import org.dljl.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.ArrayList;


@RestController
@RequestMapping("/availability")

public class AvailabilityController {

    // Make a day not available
    @PutMapping("/blockDay/{day}")
    public ResponseEntity<String> blockDay(@RequestBody String day) {
        try {
            availibility.BookDay(day);
            return ResponseEntity.ok("this day is blocked");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // get available days in a month
    @GetMapping("/availableDays/{month}")
    public ResponseEntity<String> availableDays(@RequestBody String month) {
        try {
            String days = availibility.availableDays(month);
            return ResponseEntity.ok(days);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/availableAtDay/{day}")
    public ResponseEntity<String> availableAtDay(@RequestBody String day) {
        try {
            String availablePeriods = availibility.availableAtDay(day);
            return ResponseEntity.ok(availablePeriods);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
