package io.github.akuszewska.tennislessons.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.akuszewska.tennislessons.TennisLessonsApplication;
import io.github.akuszewska.tennislessons.builder.CourseBuilder;
import io.github.akuszewska.tennislessons.domain.Course;
import io.github.akuszewska.tennislessons.domain.Role;
import io.github.akuszewska.tennislessons.domain.User;
import io.github.akuszewska.tennislessons.dto.CourseDTO;
import io.github.akuszewska.tennislessons.mapper.CourseDTOMapper;
import io.github.akuszewska.tennislessons.repository.CourseRepository;
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

import java.util.List;

import static io.github.akuszewska.tennislessons.builder.CourseBuilder.COURSE_CITY;
import static io.github.akuszewska.tennislessons.builder.CourseBuilder.COURSE_DESCRIPTION;
import static io.github.akuszewska.tennislessons.builder.CourseBuilder.COURSE_MAX_PARTICIPANTS;
import static io.github.akuszewska.tennislessons.builder.CourseBuilder.COURSE_PRICE;
import static io.github.akuszewska.tennislessons.builder.CourseBuilder.COURSE_TITLE;
import static io.github.akuszewska.tennislessons.builder.CourseBuilder.COURSE_UPDATED_TITLE;
import static io.github.akuszewska.tennislessons.builder.CourseBuilder.createDefaultCourseCreationBody;
import static io.github.akuszewska.tennislessons.builder.RoleBuilder.createInstructorRole;
import static io.github.akuszewska.tennislessons.builder.UserBuilder.createDefaultInstructor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude= SecurityAutoConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TennisLessonsApplication.class)
public class CourseRestControllerIntegrationTest {

    private static final String CONTROLLER_BASE_URL = "/api/course";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CourseDTOMapper courseDTOMapper;

    @MockBean
    private AuthService authService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        objectMapper = new ObjectMapper();

        courseRepository.flush();
        userRepository.flush();
        roleRepository.flush();
    }

    @Test
    @WithMockUser(username = "student", roles = "STUDENT")
    public void getCoursesByCity_thenReturns200StatusCode() throws Exception {

        createDefaultCourse();

        mvc.perform(get(CONTROLLER_BASE_URL.concat("/").concat(CourseBuilder.COURSE_CITY))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is(COURSE_TITLE)))
                .andExpect(jsonPath("$[0].description", is(COURSE_DESCRIPTION)))
                .andExpect(jsonPath("$[0].maxParticipants", is(COURSE_MAX_PARTICIPANTS)))
                .andExpect(jsonPath("$[0].city", is(COURSE_CITY)))
                .andExpect(jsonPath("$[0].price", is(COURSE_PRICE)))
                .andExpect(jsonPath("$[0].active", is(Boolean.TRUE)));
    }

    @Test
    @WithMockUser(username = "instructor", roles = "INSTRUCTOR")
    public void createCourse_thenReturn201Status() throws Exception {

        when(authService.getCurrentLoggedInUser()).thenReturn(createInstructor());

        mvc.perform(post(CONTROLLER_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDefaultCourseCreationBody())))
                .andDo(print())
                .andExpect(status().isCreated());

        List<Course> courses = courseRepository.findAll();
        assertThat(courses).extracting(Course::getTitle).containsOnly(COURSE_TITLE);
    }

    @Test
    @WithMockUser(username = "instructor", roles = "INSTRUCTOR")
    public void updateCourse_thenReturn200StatusCode() throws Exception {

        Course existingCourse = createDefaultCourse();
        existingCourse.setTitle(COURSE_UPDATED_TITLE);
        CourseDTO updateRequest = courseDTOMapper.map(existingCourse);

        mvc.perform(put(CONTROLLER_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId", is(existingCourse.getId().intValue())))
                .andExpect(jsonPath("$.title", is(COURSE_UPDATED_TITLE)));
    }

    @Test
    @WithMockUser(username = "instructor", roles = "INSTRUCTOR")
    public void deleteCourse_thenReturns200StatusCode() throws Exception {

        Long courseId = createDefaultCourse().getId();

        mvc.perform(delete(CONTROLLER_BASE_URL.concat("/").concat(courseId.toString()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(COURSE_TITLE)))
                .andExpect(jsonPath("$.description", is(COURSE_DESCRIPTION)))
                .andExpect(jsonPath("$.maxParticipants", is(COURSE_MAX_PARTICIPANTS)))
                .andExpect(jsonPath("$.city", is(COURSE_CITY)))
                .andExpect(jsonPath("$.price", is(COURSE_PRICE)))
                .andExpect(jsonPath("$.active", is(Boolean.TRUE)));

        assertThat(courseRepository.count()).isZero();
    }

    private Course createDefaultCourse() {

        User instructorUser = createInstructor();
        Course course = CourseBuilder.createDefaultCourse();
        course.setInstructor(instructorUser);
        return courseRepository.save(course);
    }

    private User createInstructor() {

        Role instructorRole = createInstructorRole();
        roleRepository.save(instructorRole);

        User instructorUser = createDefaultInstructor();
        instructorUser.setRole(instructorRole);
        userRepository.save(instructorUser);

        return instructorUser;
    }
}


