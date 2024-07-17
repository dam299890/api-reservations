package com.microservice.apireservations.mapper;

import org.springframework.core.convert.converter.Converter;
import com.microservice.apireservations.dto.ReservationDTO;
import com.microservice.apireservations.model.Reservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationDTOMapper extends Converter<ReservationDTO, Reservation> {

    @Override
    Reservation convert(ReservationDTO source);

}