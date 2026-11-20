package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.SensorReadingDto;
import com.aldisued.iot.monitoring.entity.Alert;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.entity.SensorReading;
import com.aldisued.iot.monitoring.exception.SensorNotFoundException;
import com.aldisued.iot.monitoring.mapper.AlertMapper;
import com.aldisued.iot.monitoring.repository.AlertRepository;
import com.aldisued.iot.monitoring.repository.SensorReadingRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;
import org.springframework.stereotype.Service;

@Service
public class SensorReadingService {

  private final SensorReadingRepository sensorReadingRepository;
  private final SensorRepository sensorRepository;
  private final AlertRepository alertRepository;

  public SensorReadingService(SensorReadingRepository sensorReadingRepository,
                              SensorRepository sensorRepository,
                              AlertRepository alertRepository) {
    this.sensorReadingRepository = sensorReadingRepository;
    this.sensorRepository = sensorRepository;
    this.alertRepository = alertRepository;
  }


  public SensorReading saveSensorReading(SensorReadingDto sensorReadingDto) {
    Sensor sensor = getSensor(sensorReadingDto);
    SensorReading savedReading = sensorReadingRepository.save(mapSensorReading(sensorReadingDto, sensor));

    Double value = savedReading.getValue();

    String alertMessage = validateReading(sensor, value);
    if (alertMessage != null) {
      saveAlert(alertMessage, sensor, savedReading);
    }

    return savedReading;
  }

  private static SensorReading mapSensorReading(SensorReadingDto sensorReadingDto, Sensor sensor) {
    return new SensorReading(sensorReadingDto.value(),
            sensorReadingDto.timestamp(),
            sensor);
  }

  private void saveAlert(String alertMessage, Sensor sensor, SensorReading savedReading) {
    Alert alert = new Alert();
    alert.setSensor(sensor);
    alert.setMessage(alertMessage);
    alert.setTimestamp(savedReading.getTimestamp());

    alertRepository.save(alert);
  }

  private static String validateReading(Sensor sensor, Double value) {
      return switch (sensor.getType()) {
        case TEMPERATURE -> value > 40.0 || value < -20.0
                ? "Temperature out of range: " + value : null;

        case HUMIDITY -> value > 90.0
                ? "Humidity too high: " + value : null;

        case ATMOSPHERIC_PRESSURE -> value < 950.0 || value > 1050.0
                ? "Pressure out of range: " + value : null;
      };
  }

  private Sensor getSensor(SensorReadingDto sensorReadingDto) {
      return sensorRepository.findById(sensorReadingDto.sensorId())
              .orElseThrow(() -> new SensorNotFoundException("Sensor not found with id: " + sensorReadingDto.sensorId()));
  }
}
