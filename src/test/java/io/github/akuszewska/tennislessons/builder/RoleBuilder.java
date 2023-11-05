package io.github.akuszewska.tennislessons.builder;

import io.github.akuszewska.tennislessons.domain.Role;
import io.github.akuszewska.tennislessons.domain.dictionary.RoleEnum;

public class RoleBuilder {

    public static final Integer INSTRUCTOR_ROLE_ID = 1;
    public static final Integer STUDENT_ROLE_ID = 2;

    public static Role createInstructorRole() {
        Role role = new Role();
        role.setId(INSTRUCTOR_ROLE_ID);
        role.setName(RoleEnum.ROLE_INSTRUCTOR);
        return role;
    }

    public static Role createStudentRole() {
        Role role = new Role();
        role.setId(STUDENT_ROLE_ID);
        role.setName(RoleEnum.ROLE_STUDENT);
        return role;
    }
}
