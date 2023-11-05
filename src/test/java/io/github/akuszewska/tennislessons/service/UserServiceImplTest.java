package io.github.akuszewska.tennislessons.service;

import io.github.akuszewska.tennislessons.TestConfiguration;
import io.github.akuszewska.tennislessons.builder.RoleBuilder;
import io.github.akuszewska.tennislessons.builder.UserBuilder;
import io.github.akuszewska.tennislessons.domain.Role;
import io.github.akuszewska.tennislessons.domain.User;
import io.github.akuszewska.tennislessons.domain.dictionary.RoleEnum;
import io.github.akuszewska.tennislessons.dto.ProfileDTO;
import io.github.akuszewska.tennislessons.repository.RoleRepository;
import io.github.akuszewska.tennislessons.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static io.github.akuszewska.tennislessons.builder.RoleBuilder.STUDENT_ROLE_ID;
import static io.github.akuszewska.tennislessons.builder.UserBuilder.STUDENT_PHONE;
import static io.github.akuszewska.tennislessons.builder.UserBuilder.STUDENT_USERNAME;
import static io.github.akuszewska.tennislessons.exception.ErrorMessage.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(classes = TestConfiguration.class)
class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @MockBean
    private AuthService authService;


    @BeforeEach
    void setUp() {

        userRepository.flush();
        roleRepository.flush();
    }

    @Test
    void getProfile_happyPath() {

        //given
        when(authService.getCurrentLoggedInUser()).thenReturn(createStudent());

        //when
        ProfileDTO actualResult = userService.getProfile();

        //then
        assertNotNull(actualResult.getUserId());
        assertEquals(STUDENT_USERNAME, actualResult.getUsername());
        assertEquals(STUDENT_PHONE, actualResult.getPhone());
        assertEquals(STUDENT_USERNAME, actualResult.getUsername());
        assertNotNull(actualResult.getRole());
        assertEquals(STUDENT_ROLE_ID, actualResult.getRole().getRoleId());
        assertEquals(RoleEnum.ROLE_STUDENT.getName(), actualResult.getRole().getRoleName());

    }

    @Test
    void getProfileById() {

        //given
        User user = createStudent();
        when(authService.getCurrentLoggedInUser()).thenReturn(user);

        //when
        ProfileDTO actualResult = userService.getProfileById(user.getId());

        //then
        assertNotNull(actualResult.getUserId());
        assertEquals(STUDENT_USERNAME, actualResult.getUsername());
        assertEquals(STUDENT_PHONE, actualResult.getPhone());
        assertEquals(STUDENT_USERNAME, actualResult.getUsername());
        assertNotNull(actualResult.getRole());
        assertEquals(STUDENT_ROLE_ID, actualResult.getRole().getRoleId());
        assertEquals(RoleEnum.ROLE_STUDENT.getName(), actualResult.getRole().getRoleName());
    }

    @Test
    void getProfileById_throwsUserNotFoundExceptionTest() {

        //given
        Long fakeUserID = -10L;

        //when
        Exception exception = assertThrows(RuntimeException.class, () -> userService.getProfileById(fakeUserID));

        //then
        assertEquals(USER_NOT_FOUND, exception.getMessage());
    }

    private User createStudent() {

        Role studentRole = RoleBuilder.createStudentRole();
        roleRepository.save(studentRole);

        User student = UserBuilder.createDefaultStudent();
        student.setRole(studentRole);
        userRepository.save(student);

        return student;
    }
}