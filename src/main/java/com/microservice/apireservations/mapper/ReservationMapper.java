package com.microservice.apireservations.mapper;

import org.springframework.core.convert.converter.Converter;
import com.microservice.apireservations.dto.ReservationDTO;
import com.microservice.apireservations.model.Reservation;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface ReservationMapper extends Converter<Reservation, ReservationDTO> {

    @Override
    ReservationDTO convert(Reservation source);
}
