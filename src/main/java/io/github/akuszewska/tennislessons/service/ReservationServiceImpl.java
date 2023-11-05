package io.github.akuszewska.tennislessons.service;

import io.github.akuszewska.tennislessons.domain.Course;
import io.github.akuszewska.tennislessons.domain.Reservation;
import io.github.akuszewska.tennislessons.domain.User;
import io.github.akuszewska.tennislessons.dto.ReservationDTO;
import io.github.akuszewska.tennislessons.mapper.ReservationDTOMapper;
import io.github.akuszewska.tennislessons.repository.CourseRepository;
import io.github.akuszewska.tennislessons.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static io.github.akuszewska.tennislessons.exception.ErrorMessage.COURSE_NOT_FOUND;
import static io.github.akuszewska.tennislessons.exception.ErrorMessage.COURSE_SOLD_OUT;
import static io.github.akuszewska.tennislessons.exception.ErrorMessage.RESERVATION_ACCEPTED;
import static io.github.akuszewska.tennislessons.exception.ErrorMessage.RESERVATION_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final CourseRepository courseRepository;
    private final AuthService authService;
    private final ReservationDTOMapper reservationMapper;

    @Override
    public List<ReservationDTO> getAcceptedReservations() {

        User client = authService.getCurrentLoggedInUser();

        return reservationRepository.findAllByClientAndAccepted(client, Boolean.TRUE)
                .stream()
                .map(reservationMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> getClientReservations() {

        User client = authService.getCurrentLoggedInUser();

        return reservationRepository.findAllByClient(client)
                .stream()
                .map(reservationMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> getInstructorReservations() {
        User instructor = authService.getCurrentLoggedInUser();

        return reservationRepository.findAllByCourseInstructor(instructor)
                .stream()
                .map(reservationMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationDTO makeReservation(ReservationDTO reservationDTO) {

        Course course = courseRepository.findById(reservationDTO.getCourseId())
                .orElseThrow(() -> new RuntimeException(COURSE_NOT_FOUND));

        Reservation reservation = Reservation.builder()
                .course(course)
                .additionalRemarks(reservationDTO.getAdditionalRemarks())
                .client(authService.getCurrentLoggedInUser())
                .accepted(Boolean.FALSE)
                .build();
        reservationRepository.save(reservation);
        return reservationMapper.map(reservation);
    }

    @Override
    public ReservationDTO deleteReservation(Long id) {

        Reservation deleted = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(RESERVATION_NOT_FOUND));

        if (deleted.getAccepted()) {
            throw new RuntimeException(RESERVATION_ACCEPTED);
        }

        reservationRepository.deleteById(id);
        return reservationMapper.map(deleted);
    }

    @Override
    public ReservationDTO acceptReservation(Long id) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(RESERVATION_NOT_FOUND));
        reservation.setAccepted(Boolean.TRUE);
        reservationRepository.save(reservation);

        Course course = reservation.getCourse();
        Integer purchased = course.getPurchased();

        if (purchased.equals(course.getMaxParticipants())) {
            throw new RuntimeException(COURSE_SOLD_OUT);
        } else if (purchased.equals(course.getMaxParticipants()-1)) {
            course.setActive(Boolean.FALSE);
        }

        course.setPurchased(purchased + 1);
        courseRepository.save(course);

        return reservationMapper.map(reservation);
    }
}
