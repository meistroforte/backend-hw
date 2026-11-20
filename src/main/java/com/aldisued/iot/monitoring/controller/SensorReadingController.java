package com.aldisued.iot.monitoring.controller;

import com.aldisued.iot.monitoring.dto.SensorReadingDto;
import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.exception.SensorNotFoundException;
import com.aldisued.iot.monitoring.service.SensorReadingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sensor-readings")
public class SensorReadingController {

  private final SensorReadingService sensorReadingService;

  public SensorReadingController(SensorReadingService sensorReadingService) {
    this.sensorReadingService = sensorReadingService;
  }

  @PostMapping
  public ResponseEntity<?> saveSensorReading(@RequestBody SensorReadingDto sensorReadingDto) {
    try {
      SensorReading sensorReading = sensorReadingService.saveSensorReading(sensorReadingDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(sensorReading);
    } catch (SensorNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
