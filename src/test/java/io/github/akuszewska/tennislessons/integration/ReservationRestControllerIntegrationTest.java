package io.github.akuszewska.tennislessons.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.akuszewska.tennislessons.TennisLessonsApplication;
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
import io.github.akuszewska.tennislessons.service.AuthService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static io.github.akuszewska.tennislessons.builder.ReservationBuilder.ADDITIONAL_RESERVATION_REMARKS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude= SecurityAutoConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TennisLessonsApplication.class)
public class ReservationRestControllerIntegrationTest {

    private static final String CONTROLLER_BASE_URL = "/api/reservation";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockBean
    private AuthService authService;

    private User instructor;
    private User student;

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
    @WithMockUser(username = "student", roles = "STUDENT")
    public void getAcceptedReservations_thenReturns200StatusCode() throws Exception {

        createReservation(Boolean.TRUE);

        mvc.perform(get(CONTROLLER_BASE_URL.concat("/accepted"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].additionalRemarks", is(ADDITIONAL_RESERVATION_REMARKS)))
                .andExpect(jsonPath("$[0].accepted", is(Boolean.TRUE)));
    }

    @Test
    @WithMockUser(username = "student", roles = "STUDENT")
    public void getClientReservations_thenReturns200StatusCode() throws Exception {

        createReservation(Boolean.TRUE);
        createReservation(Boolean.FALSE);

        mvc.perform(get(CONTROLLER_BASE_URL.concat("/client-all"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(username = "instructor", roles = "INSTRUCTOR")
    public void getInstructorReservations_thenReturns200StatusCode() throws Exception {

        Reservation reservation = createReservation(Boolean.FALSE);

        when(authService.getCurrentLoggedInUser()).thenReturn(instructor);

        mvc.perform(get(CONTROLLER_BASE_URL.concat("/instructor"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].reservationId", is(reservation.getId().intValue())))
                .andExpect(jsonPath("$[0].additionalRemarks", is(reservation.getAdditionalRemarks())))
                .andExpect(jsonPath("$[0].accepted", is(reservation.getAccepted())))
                .andExpect(jsonPath("$[0].clientId", is(reservation.getClient().getId().intValue())))
                .andExpect(jsonPath("$[0].courseId", is(reservation.getCourse().getId().intValue())));
    }

    @Test
    @WithMockUser(username = "student", roles = "STUDENT")
    public void makeReservation_thenReturn201StatusCode() throws Exception {

        Long courseId = createCourse(instructor).getId();
        ReservationDTO requestBody = ReservationBuilder.createReservationCreationBody(courseId);

        ObjectMapper objectMapper = new ObjectMapper();

        mvc.perform(post(CONTROLLER_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservationId", notNullValue()))
                .andExpect(jsonPath("$.additionalRemarks", is(ADDITIONAL_RESERVATION_REMARKS)))
                .andExpect(jsonPath("$.accepted", is(Boolean.FALSE)))
                .andExpect(jsonPath("$.clientId", is(student.getId().intValue())))
                .andExpect(jsonPath("$.courseId", is(courseId.intValue())));
    }

    @Test
    @WithMockUser(username = "student", roles = "STUDENT")
    public void deleteReservation_thenReturn200StatusCode() throws Exception {

        Reservation reservation = createReservation(Boolean.FALSE);

        mvc.perform(delete(CONTROLLER_BASE_URL.concat("/").concat(String.valueOf(reservation.getId().intValue())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(reservationRepository.count()).isZero();
    }

    @Test
    @WithMockUser(username = "instructor", roles = "INSTRUCTOR")
    public void acceptReservation_thenReturn200StatusCode() throws Exception {

        Reservation reservation = createReservation(Boolean.FALSE);

        mvc.perform(patch(CONTROLLER_BASE_URL.concat("/").concat(String.valueOf(reservation.getId().intValue())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId", is(reservation.getId().intValue())))
                .andExpect(jsonPath("$.additionalRemarks", is(reservation.getAdditionalRemarks())))
                .andExpect(jsonPath("$.accepted", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.clientId", is(reservation.getClient().getId().intValue())))
                .andExpect(jsonPath("$.courseId", is(reservation.getCourse().getId().intValue())));
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
