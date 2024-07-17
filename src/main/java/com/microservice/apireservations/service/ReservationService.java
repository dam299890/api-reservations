package com.microservice.apireservations.service;



import com.microservice.apireservations.connector.CatalogConnector;
import com.microservice.apireservations.connector.response.CityDTO;
import com.microservice.apireservations.dto.ReservationDTO;
import com.microservice.apireservations.dto.SegmentDTO;
import com.microservice.apireservations.enums.APIError;
import com.microservice.apireservations.exception.EdteamException;
import com.microservice.apireservations.model.Reservation;
import com.microservice.apireservations.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReservationService {

    private ReservationRepository repository;

    private ConversionService conversionService;

    private CatalogConnector connector;

    @Autowired
    public ReservationService(ReservationRepository repository,
                              ConversionService conversionService,
                              CatalogConnector connector) {
        this.repository = repository;
        this.conversionService = conversionService;
        this.connector =  connector;
    }

    public List<ReservationDTO> getReservations() {
        return  conversionService.convert(repository.getReservations(), List.class);
    }

    public ReservationDTO getReservationById(Long id) {
        Optional<Reservation> result = repository.getReservationById(id);
        if(result.isEmpty()) {
            throw new EdteamException(APIError.RESERVATION_NOT_FOUND);
        }
        return conversionService.convert(result.get(), ReservationDTO.class);
    }

    public ReservationDTO save(ReservationDTO reservation) {
        if(Objects.nonNull(reservation.getId())) {
            throw new EdteamException(APIError.RESERVATION_WITH_SAME_ID);
        }
        checkCity(reservation);
        Reservation transformed = conversionService.convert(reservation, Reservation.class);
        Reservation result = repository.save(Objects.requireNonNull(transformed));
        return conversionService.convert(result, ReservationDTO.class);
    }

    public ReservationDTO update(Long id, ReservationDTO reservation) {
        if(getReservationById(id) == null) {
            throw new EdteamException(APIError.RESERVATION_NOT_FOUND);
        }
        checkCity(reservation);
        Reservation transformed = conversionService.convert(reservation, Reservation.class);
        Reservation result = repository.update(id, Objects.requireNonNull(transformed));
        return conversionService.convert(result, ReservationDTO.class);
    }

    public void delete(Long id) {
        if(getReservationById(id) == null) {
            throw new EdteamException(APIError.RESERVATION_NOT_FOUND);
        }

        repository.delete(id);
    }
    
    private void checkCity(ReservationDTO reservationDTO){

        for (SegmentDTO segmentDTO: reservationDTO.getItinerary().getSegment()
             ) {
            CityDTO origin = connector.getCity(segmentDTO.getOrigin());
            CityDTO destination = connector.getCity(segmentDTO.getDestination());

            if (origin==null || destination == null){
                throw new EdteamException(APIError.VALIDATION_ERROR);
            } else{
                System.out.println(origin.getName());
                System.out.println(destination.getName());
            }
        }
    }
}