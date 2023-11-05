package io.github.akuszewska.tennislessons.builder;

import io.github.akuszewska.tennislessons.domain.Course;
import io.github.akuszewska.tennislessons.domain.Reservation;
import io.github.akuszewska.tennislessons.domain.User;
import io.github.akuszewska.tennislessons.dto.ReservationDTO;

public class ReservationBuilder {

    public static final String ADDITIONAL_RESERVATION_REMARKS = "Additional reservation remarks";

    public static Reservation createDefaultReservation(User client, Course course, Boolean accepted) {

        return Reservation.builder()
                .course(course)
                .client(client)
                .additionalRemarks(ADDITIONAL_RESERVATION_REMARKS)
                .accepted(accepted)
                .build();
    }

    public static ReservationDTO createReservationCreationBody(Long courseId) {

        return ReservationDTO.builder()
                .courseId(courseId)
                .additionalRemarks(ADDITIONAL_RESERVATION_REMARKS)
                .build();
    }
}
