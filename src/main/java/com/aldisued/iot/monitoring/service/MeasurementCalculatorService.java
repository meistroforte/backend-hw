package com.aldisued.iot.monitoring.service;


import java.util.List;
import java.util.OptionalDouble;

import org.springframework.stereotype.Service;

@Service
public class MeasurementCalculatorService {

  public List<Double> filterByAverageDeviation(List<Double> values, Double deviation) {
    if (deviation < 0.0 || deviation > 1.0) {
      throw new IllegalArgumentException("Deviation must be between 0.0 and 1.0");
    }

    OptionalDouble average = values.stream()
            .mapToDouble(Double::doubleValue)
            .average();

    if (average.isEmpty()) {
      return List.of();
    }

    double min = average.getAsDouble() * (1 - deviation);
    double max = average.getAsDouble() * (1 + deviation);

    return values.stream()
            .filter(value -> value >= min && value <= max)
            .toList();
  }

}
