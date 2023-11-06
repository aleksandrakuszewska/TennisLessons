package io.github.akuszewska.tennislessons.integration;

import io.github.akuszewska.tennislessons.TennisLessonsApplication;
import io.github.akuszewska.tennislessons.builder.CourseBuilder;
import io.github.akuszewska.tennislessons.builder.ReservationBuilder;
import io.github.akuszewska.tennislessons.builder.RoleBuilder;
import io.github.akuszewska.tennislessons.builder.UserBuilder;
import io.github.akuszewska.tennislessons.domain.Course;
import io.github.akuszewska.tennislessons.domain.Reservation;
import io.github.akuszewska.tennislessons.domain.Role;
import io.github.akuszewska.tennislessons.domain.User;
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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private void createReservation(Boolean accepted) {

        Course course = createCourse(instructor);

        Reservation reservation = ReservationBuilder.createDefaultReservation(student, course, accepted);
        reservationRepository.save(reservation);
    }

    private Course createCourse(User instructor) {

        Course course = CourseBuilder.createDefaultCourse();
        course.setInstructor(instructor);
        return courseRepository.save(course);
    }
}
