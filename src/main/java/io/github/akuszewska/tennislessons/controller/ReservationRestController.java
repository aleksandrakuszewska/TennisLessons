package io.github.akuszewska.tennislessons.controller;

import io.github.akuszewska.tennislessons.dto.ReservationDTO;
import io.github.akuszewska.tennislessons.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.github.akuszewska.tennislessons.security.dictionary.AuthenticationDictionary.STUDENT_PERMISSION;
import static io.github.akuszewska.tennislessons.security.dictionary.AuthenticationDictionary.INSTRUCTOR_PERMISSION;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationRestController {

    private final ReservationService reservationService;

    @GetMapping("/accepted")
    @PreAuthorize(STUDENT_PERMISSION)
    public ResponseEntity<List<ReservationDTO>> getAcceptedReservations() {
        return ResponseEntity.ok(reservationService.getAcceptedReservations());
    }

    @GetMapping("/client-all")
    @PreAuthorize(STUDENT_PERMISSION)
    public ResponseEntity<List<ReservationDTO>> getClientReservations() {
        return ResponseEntity.ok(reservationService.getClientReservations());
    }

    @GetMapping("/instructor")
    @PreAuthorize(INSTRUCTOR_PERMISSION)
    public ResponseEntity<List<ReservationDTO>> getInstructorReservations() {
        return ResponseEntity.ok(reservationService.getInstructorReservations());
    }

    @PostMapping
    @PreAuthorize(STUDENT_PERMISSION)
    public ResponseEntity<ReservationDTO> makeReservation(@RequestBody ReservationDTO reservationDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservationService.makeReservation(reservationDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(STUDENT_PERMISSION)
    public ResponseEntity<ReservationDTO> deleteReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.deleteReservation(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize(INSTRUCTOR_PERMISSION)
    public ResponseEntity<ReservationDTO> acceptReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.acceptReservation(id));
    }
}
