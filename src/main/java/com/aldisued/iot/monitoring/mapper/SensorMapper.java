package com.aldisued.iot.monitoring.mapper;

import com.aldisued.iot.monitoring.dto.SensorDto;
import com.aldisued.iot.monitoring.entity.Sensor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SensorMapper {
    Sensor toEntity(SensorDto dto);
    SensorDto toDto(Sensor entity);
}
