package io.github.akuszewska.tennislessons.service;

import io.github.akuszewska.tennislessons.TestConfiguration;
import io.github.akuszewska.tennislessons.builder.CourseBuilder;
import io.github.akuszewska.tennislessons.builder.ReservationBuilder;
import io.github.akuszewska.tennislessons.builder.RoleBuilder;
import io.github.akuszewska.tennislessons.builder.UserBuilder;
import io.github.akuszewska.tennislessons.domain.Course;
import io.github.akuszewska.tennislessons.domain.Reservation;
import io.github.akuszewska.tennislessons.domain.Role;
import io.github.akuszewska.tennislessons.domain.User;
import io.github.akuszewska.tennislessons.dto.ReservationDTO;
import io.github.akuszewska.tennislessons.repository.CourseRepository;
import io.github.akuszewska.tennislessons.repository.ReservationRepository;
import io.github.akuszewska.tennislessons.repository.RoleRepository;
import io.github.akuszewska.tennislessons.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static io.github.akuszewska.tennislessons.builder.ReservationBuilder.ADDITIONAL_RESERVATION_REMARKS;
import static io.github.akuszewska.tennislessons.builder.ReservationBuilder.createReservationCreationBody;
import static io.github.akuszewska.tennislessons.exception.ErrorMessage.COURSE_NOT_FOUND;
import static io.github.akuszewska.tennislessons.exception.ErrorMessage.COURSE_SOLD_OUT;
import static io.github.akuszewska.tennislessons.exception.ErrorMessage.RESERVATION_ACCEPTED;
import static io.github.akuszewska.tennislessons.exception.ErrorMessage.RESERVATION_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(classes = TestConfiguration.class)
class ReservationServiceImplTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService reservationService;

    @MockBean
    private AuthService authService;

    private User student;
    private User instructor;

    @BeforeEach
    void setUp() {

        student = createStudent();
        instructor = createInstructor();
        when(authService.getCurrentLoggedInUser()).thenReturn(student);

        roleRepository.flush();
        userRepository.flush();
        courseRepository.flush();
        reservationRepository.flush();
    }

    @Test
    void getAcceptedReservations_happyPath() {

        //given
        createReservation(Boolean.TRUE);
        long expectedReservationAmount = 1;

        //when
        List<ReservationDTO> resultSet = reservationService.getAcceptedReservations();

        //then
        assertEquals(expectedReservationAmount, resultSet.size());
    }

    @Test
    void getClientReservations_happyPath() {

        //given
        createReservation(Boolean.FALSE);
        long expectedReservationAmount = 1;

        //when
        List<ReservationDTO> resultSet = reservationService.getClientReservations();

        //then
        assertEquals(expectedReservationAmount, resultSet.size());
    }

    @Test
    void getInstructorReservations_happyPath() {

        //given
        when(authService.getCurrentLoggedInUser()).thenReturn(instructor);
        createReservation(Boolean.FALSE);
        long expectedReservationAmount = 1;

        //when
        List<ReservationDTO> resultSet = reservationService.getInstructorReservations();

        //then
        assertEquals(expectedReservationAmount, resultSet.size());
    }

    @Test
    void makeReservation_happyPath() {

        //given
        Course course = createCourse(instructor);
        ReservationDTO reservation = createReservationCreationBody(course.getId());
        long expectedReservationsAmount = reservationRepository.count() + 1;

        //when
        ReservationDTO reservationAfterSave = reservationService.makeReservation(reservation);
        long actualReservationsAmount = reservationRepository.count();

        //then
        assertEquals(expectedReservationsAmount, actualReservationsAmount);
        assertEquals(ADDITIONAL_RESERVATION_REMARKS, reservationAfterSave.getAdditionalRemarks());
    }

    @Test
    void makeReservation_throwsCourseNotFoundExceptionTest() {

        //given
        Long courseFakeID = -10L;
        ReservationDTO reservation = createReservationCreationBody(courseFakeID);

        //when
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> reservationService.makeReservation(reservation));

        //then
        assertEquals(COURSE_NOT_FOUND, exception.getMessage());
    }

    @Test
    void deleteReservation_happyPath() {

        //given
        Reservation reservation = createReservation(Boolean.FALSE);
        long exceptedReservationAmount = reservationRepository.count() - 1;

        //when
        reservationService.deleteReservation(reservation.getId());
        long actualReservationAmount = reservationRepository.count();

        //then
        assertEquals(exceptedReservationAmount, actualReservationAmount);
    }

    @Test
    void deleteReservation_throwsReservationNotFoundExceptionTest() {

        //given
        Long fakeReservationId = -10L;

        //when
        Exception exception = assertThrows(RuntimeException.class, () -> reservationService.deleteReservation(fakeReservationId));

        //then
        assertEquals(RESERVATION_NOT_FOUND, exception.getMessage());
    }

    @Test
    void deleteReservation_throwsReservationAcceptedExceptionTest() {

        //given
        Reservation reservation = createReservation(Boolean.TRUE);

        //when
        Exception exception = assertThrows(RuntimeException.class, () -> reservationService.deleteReservation(reservation.getId()));

        //then
        assertEquals(RESERVATION_ACCEPTED, exception.getMessage());
    }

    @Test
    void acceptReservation_happyPath() {

        //given
        Reservation reservation = createReservation(Boolean.FALSE);

        //when
        ReservationDTO reservationAfterAccept = reservationService.acceptReservation(reservation.getId());

        //then
        assertEquals(Boolean.TRUE, reservationAfterAccept.getAccepted());
    }

    @Test
    void acceptReservation_throwsReservationNotFoundExceptionTest() {

        //given
        Long fakeReservationId = -10L;

        //when
        Exception exception = assertThrows(RuntimeException.class, () -> reservationService.acceptReservation(fakeReservationId));

        //then
        assertEquals(RESERVATION_NOT_FOUND, exception.getMessage());
    }

    @Test
    void acceptReservation_throwsCourseSoldOutExceptionTest() {

        //given
        Reservation reservation = createReservation(Boolean.FALSE);
        Course soldOutCourse = reservation.getCourse();
        soldOutCourse.setPurchased(15);
        courseRepository.save(soldOutCourse);

        //when
        Exception exception = assertThrows(RuntimeException.class, () -> reservationService.acceptReservation(reservation.getId()));

        //then
        assertEquals(COURSE_SOLD_OUT, exception.getMessage());
    }

    private User createStudent() {

        Role studentRole = RoleBuilder.createStudentRole();
        roleRepository.save(studentRole);

        User student = UserBuilder.createDefaultStudent();
        student.setRole(studentRole);
        userRepository.save(student);

        return student;
    }

    private User createInstructor() {

        Role instructorRole = RoleBuilder.createInstructorRole();
        roleRepository.save(instructorRole);

        User instructor = UserBuilder.createDefaultInstructor();
        instructor.setRole(instructorRole);
        userRepository.save(instructor);

        return instructor;
    }

    private Reservation createReservation(Boolean accepted) {

        Course course = createCourse(instructor);

        Reservation reservation = ReservationBuilder.createDefaultReservation(student, course, accepted);
        return reservationRepository.save(reservation);
    }

    private Course createCourse(User instructor) {

        Course course = CourseBuilder.createDefaultCourse();
        course.setInstructor(instructor);
        return courseRepository.save(course);
    }
}