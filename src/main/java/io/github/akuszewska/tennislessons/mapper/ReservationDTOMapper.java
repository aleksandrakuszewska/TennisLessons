package io.github.akuszewska.tennislessons.mapper;

import io.github.akuszewska.tennislessons.domain.Reservation;
import io.github.akuszewska.tennislessons.dto.ReservationDTO;
import org.springframework.stereotype.Component;

@Component
public class ReservationDTOMapper {

    public ReservationDTO map(Reservation from) {
        return ReservationDTO.builder()
                .reservationId(from.getId())
                .courseId(from.getCourse().getId())
                .accepted(from.getAccepted())
                .clientId(from.getClient().getId())
                .additionalRemarks(from.getAdditionalRemarks())
                .build();
    }
}
