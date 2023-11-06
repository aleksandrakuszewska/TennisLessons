package io.github.akuszewska.tennislessons.integration;

import io.github.akuszewska.tennislessons.TennisLessonsApplication;
import io.github.akuszewska.tennislessons.builder.RoleBuilder;
import io.github.akuszewska.tennislessons.builder.UserBuilder;
import io.github.akuszewska.tennislessons.domain.Role;
import io.github.akuszewska.tennislessons.domain.User;
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

import static io.github.akuszewska.tennislessons.builder.RoleBuilder.STUDENT_ROLE_ID;
import static io.github.akuszewska.tennislessons.builder.UserBuilder.STUDENT_PHONE;
import static io.github.akuszewska.tennislessons.builder.UserBuilder.STUDENT_USERNAME;
import static io.github.akuszewska.tennislessons.domain.dictionary.RoleEnum.ROLE_STUDENT;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude= SecurityAutoConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TennisLessonsApplication.class)
public class UserRestControllerIntegrationTest {

    private static final String BASE_CONTROLLER_URL = "/api/profile";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @MockBean
    private AuthService authService;

    @BeforeEach
    void setUp() {

        userRepository.flush();
        roleRepository.flush();
    }

    @Test
    @WithMockUser(username = "student", roles = "STUDENT")
    public void getProfile_thenReturn200StatusCode() throws Exception {

        when(authService.getCurrentLoggedInUser()).thenReturn(createStudent());

        mvc.perform(get(BASE_CONTROLLER_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(STUDENT_USERNAME)))
                .andExpect(jsonPath("$.phone", is(STUDENT_PHONE)))
                .andExpect(jsonPath("$.role.roleId", is(STUDENT_ROLE_ID)))
                .andExpect(jsonPath("$.role.roleName", is(ROLE_STUDENT.getName())));
    }

    @Test
    @WithMockUser(username = "student", roles = "STUDENT")
    public void getProfileById_thenReturn200StatusCode() throws Exception {

        User user = createStudent();
        when(authService.getCurrentLoggedInUser()).thenReturn(user);

        mvc.perform(get(BASE_CONTROLLER_URL.concat("/".concat(String.valueOf(user.getId().intValue()))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(STUDENT_USERNAME)))
                .andExpect(jsonPath("$.phone", is(STUDENT_PHONE)))
                .andExpect(jsonPath("$.role.roleId", is(STUDENT_ROLE_ID)))
                .andExpect(jsonPath("$.role.roleName", is(ROLE_STUDENT.getName())));
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
