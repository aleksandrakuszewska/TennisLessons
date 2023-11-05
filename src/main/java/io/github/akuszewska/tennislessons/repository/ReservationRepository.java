package io.github.akuszewska.tennislessons.repository;

import io.github.akuszewska.tennislessons.domain.Reservation;
import io.github.akuszewska.tennislessons.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByCourseInstructor(User user);
    List<Reservation> findAllByClient(User user);
    List<Reservation> findAllByClientAndAccepted(User user, Boolean active);
}
