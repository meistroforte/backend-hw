package com.aldisued.iot.monitoring.service;

import com.aldisued.iot.monitoring.dto.AlertDto;
import com.aldisued.iot.monitoring.entity.Alert;
import com.aldisued.iot.monitoring.entity.Sensor;
import com.aldisued.iot.monitoring.exception.AlertNotFoundException;
import com.aldisued.iot.monitoring.exception.SensorNotFoundException;
import com.aldisued.iot.monitoring.mapper.AlertMapper;
import com.aldisued.iot.monitoring.repository.AlertRepository;
import com.aldisued.iot.monitoring.repository.SensorRepository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlertService {

  private final AlertRepository alertRepository;
  private final SensorRepository sensorRepository;
  private final KafkaTemplate<String, AlertDto> kafkaTemplate;
  private final AlertMapper alertMapper;

  public AlertService(AlertRepository alertRepository, SensorRepository sensorRepository,
                      KafkaTemplate<String, AlertDto> kafkaTemplate, AlertMapper alertMapper) {
    this.alertRepository = alertRepository;
    this.sensorRepository = sensorRepository;
    this.kafkaTemplate = kafkaTemplate;
    this.alertMapper = alertMapper;
  }

  public Alert saveAlert(AlertDto alertDto) {
    Sensor sensor = getSensor(alertDto);

    Alert savedAlert = alertRepository.save(toEntity(alertDto, sensor));

    kafkaTemplate.send("alerts", alertDto);

    return savedAlert;
  }

  private Alert toEntity(AlertDto alertDto, Sensor sensor) {
    Alert alert = new Alert();
    alert.setSensor(sensor);
    alert.setMessage(alertDto.message());
    alert.setTimestamp(alertDto.timestamp());
    return alert;
  }

  private Sensor getSensor(AlertDto alertDto) {
    Optional<Sensor> optional = sensorRepository.findById(alertDto.sensorId());
    if (optional.isEmpty()) {
      throw new SensorNotFoundException("Sensor not found by ID: " + alertDto.sensorId());
    }
    return optional.get();
  }

  public AlertDto findLastAlertBySensorId(UUID sensorId) {
    Alert alert = alertRepository.findFirstBySensorIdOrderByTimestampDesc(sensorId)
            .orElseThrow(() -> new AlertNotFoundException("Alert not found by Sensor ID: " + sensorId));

    return alertMapper.toDto(alert);
  }
}
