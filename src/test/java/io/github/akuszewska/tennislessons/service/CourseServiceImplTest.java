package io.github.akuszewska.tennislessons.service;

import io.github.akuszewska.tennislessons.TestConfiguration;
import io.github.akuszewska.tennislessons.builder.CourseBuilder;
import io.github.akuszewska.tennislessons.builder.RoleBuilder;
import io.github.akuszewska.tennislessons.domain.Course;
import io.github.akuszewska.tennislessons.domain.Role;
import io.github.akuszewska.tennislessons.domain.User;
import io.github.akuszewska.tennislessons.dto.CourseDTO;
import io.github.akuszewska.tennislessons.mapper.CourseDTOMapper;
import io.github.akuszewska.tennislessons.repository.CourseRepository;
import io.github.akuszewska.tennislessons.repository.RoleRepository;
import io.github.akuszewska.tennislessons.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static io.github.akuszewska.tennislessons.builder.CourseBuilder.COURSE_CITY;
import static io.github.akuszewska.tennislessons.builder.CourseBuilder.COURSE_DESCRIPTION;
import static io.github.akuszewska.tennislessons.builder.CourseBuilder.COURSE_MAX_PARTICIPANTS;
import static io.github.akuszewska.tennislessons.builder.CourseBuilder.COURSE_PRICE;
import static io.github.akuszewska.tennislessons.builder.CourseBuilder.COURSE_TITLE;
import static io.github.akuszewska.tennislessons.builder.CourseBuilder.COURSE_UPDATED_TITLE;
import static io.github.akuszewska.tennislessons.builder.CourseBuilder.createCourseCreationBodyWithDatesWithInvalidDates;
import static io.github.akuszewska.tennislessons.builder.CourseBuilder.createDefaultCourseCreationBody;
import static io.github.akuszewska.tennislessons.builder.UserBuilder.createDefaultInstructor;
import static io.github.akuszewska.tennislessons.exception.ErrorMessage.COURSE_NOT_FOUND;
import static io.github.akuszewska.tennislessons.exception.ErrorMessage.DATES_ORDER_INVALID;
import static io.github.akuszewska.tennislessons.exception.ErrorMessage.PURCHASED_COURSE_DELETION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestConfiguration.class)
@Transactional
class CourseServiceImplTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @MockBean
    private AuthService authService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseDTOMapper courseDTOMapper;

    @BeforeEach
    void setUp() {

        roleRepository.flush();
        userRepository.flush();
        courseRepository.flush();
    }

    @Test
    void getCoursesByCity_happyPath() {

        //given
        createDefaultCourse();
        int expectedCourseAmount = 1;

        //when
        List<CourseDTO> resultSet = courseService.getCoursesByCity(COURSE_CITY);

        //then
        assertEquals(expectedCourseAmount, resultSet.size());
    }

    @Test
    void createCourse_happyPath() {

        //given
        when(authService.getCurrentLoggedInUser()).thenReturn(createInstructor());
        long expectedCoursesAmount = courseRepository.count() + 1;

        //when
        CourseDTO actualCreatedCourse = courseService.createCourse(createDefaultCourseCreationBody());
        long actualCoursesAmount = courseRepository.count();

        //then
        assertEquals(expectedCoursesAmount, actualCoursesAmount);
        assertEquals(COURSE_TITLE, actualCreatedCourse.getTitle());
        assertEquals(COURSE_DESCRIPTION, actualCreatedCourse.getDescription());
        assertEquals(COURSE_CITY, actualCreatedCourse.getCity());
        assertEquals(COURSE_MAX_PARTICIPANTS, actualCreatedCourse.getMaxParticipants());
        assertEquals(COURSE_PRICE, actualCreatedCourse.getPrice());
    }

    @Test
    void createCourse_throwsDatesInvalidOrderExceptionTest() {

        //given
        CourseDTO courseWithInvalidDatesOrder = createCourseCreationBodyWithDatesWithInvalidDates();

        //when
        Exception exception = assertThrows(RuntimeException.class, () -> courseService.createCourse(courseWithInvalidDatesOrder));

        //then
        assertEquals(DATES_ORDER_INVALID, exception.getMessage());
    }

    @Test
    void deleteCourse_happyPath() {

        //given
        Course course = createDefaultCourse();
        long expectedCoursesAmount = courseRepository.count() - 1;

        //when
        courseService.deleteCourse(course.getId());
        long actualCoursesAmount = courseRepository.count();

        //then
        assertEquals(expectedCoursesAmount, actualCoursesAmount);
    }

    @Test
    void deleteCourse_throwsCoursesNotFoundExceptionTest() {

        //given
        Long fakeCourseId = -10L;

        //when
        Exception exception = assertThrows(RuntimeException.class, () -> courseService.deleteCourse(fakeCourseId));

        //then
        assertEquals(COURSE_NOT_FOUND, exception.getMessage());
    }

    @Test
    void deleteCourse_throwsCourseWasPurchasedExceptionTest() {

        //given
        Course course = createPurchasedCourse();

        //when
        Exception exception = assertThrows(RuntimeException.class, () -> courseService.deleteCourse(course.getId()));

        //then
        assertEquals(PURCHASED_COURSE_DELETION, exception.getMessage());
    }

    @Test
    void updateCourse_happyPath() {

        //given
        Course course = createDefaultCourse();
        CourseDTO courseBeforeUpdate = courseDTOMapper.map(course);
        courseBeforeUpdate.setTitle(COURSE_UPDATED_TITLE);

        //when
        CourseDTO courseAfterUpdate = courseService.updateCourse(courseBeforeUpdate);

        //then
        assertEquals(COURSE_UPDATED_TITLE, courseAfterUpdate.getTitle());
    }

    @Test
    void updateCourse_throwsInvalidDatesOrderExceptionTest() {

        //given
        Course course = createDefaultCourse();
        CourseDTO courseWithInvalidDatesOrder = courseDTOMapper.map(course);

        Date dateBefore = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date dateAfter = Date.from(LocalDate.ofInstant(dateBefore.toInstant(), ZoneId.systemDefault()).plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant());

        courseWithInvalidDatesOrder.setStart(dateAfter);
        courseWithInvalidDatesOrder.setStop(dateBefore);

        //when
        Exception exception = assertThrows(RuntimeException.class, () -> courseService.updateCourse(courseWithInvalidDatesOrder));

        //then
        assertEquals(DATES_ORDER_INVALID, exception.getMessage());
    }

    @Test
    void updateCourse_throwsCourseNotFoundExceptionTest() {

        //given
        CourseDTO courseDTOWithFakeCourseID = CourseDTO.builder().courseId(-10L).build();

        //when
        Exception exception = assertThrows(RuntimeException.class, () -> courseService.updateCourse(courseDTOWithFakeCourseID));

        //then
        assertEquals(COURSE_NOT_FOUND, exception.getMessage());
    }

    private Course createDefaultCourse() {

        User instructorUser = createInstructor();
        Course course = CourseBuilder.createDefaultCourse();
        course.setInstructor(instructorUser);
        courseRepository.save(course);
        return course;
    }

    private User createInstructor() {

        Role instructorRole = RoleBuilder.createInstructorRole();
        roleRepository.save(instructorRole);

        User instructorUser = createDefaultInstructor();
        instructorUser.setRole(instructorRole);
        userRepository.save(instructorUser);

        return instructorUser;
    }

    private Course createPurchasedCourse() {

        Course course = createDefaultCourse();
        course.setPurchased(1);
        courseRepository.save(course);
        return course;
    }
}