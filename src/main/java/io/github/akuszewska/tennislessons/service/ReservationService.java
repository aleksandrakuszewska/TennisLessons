package io.github.akuszewska.tennislessons.service;

import io.github.akuszewska.tennislessons.dto.ReservationDTO;

import java.util.List;

public interface ReservationService {

    List<ReservationDTO> getAcceptedReservations();
    List<ReservationDTO> getClientReservations();
    List<ReservationDTO> getInstructorReservations();
    ReservationDTO makeReservation(ReservationDTO reservationDTO);
    ReservationDTO deleteReservation(Long id);
    ReservationDTO acceptReservation(Long id);
}
