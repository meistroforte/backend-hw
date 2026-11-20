package com.aldisued.iot.monitoring.controller;

import com.aldisued.iot.monitoring.exception.AlertNotFoundException;
import com.aldisued.iot.monitoring.service.AlertService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/alerts")
@Validated
public class AlertController {

  private final AlertService alertService;

  public AlertController(AlertService alertService) {
    this.alertService = alertService;
  }

  @GetMapping("/latest")
  public ResponseEntity<?> findLastAlertBySensorId(@RequestParam UUID sensorId) {
    try {
      return ResponseEntity.ok().body(alertService.findLastAlertBySensorId(sensorId));
    } catch (AlertNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

}
