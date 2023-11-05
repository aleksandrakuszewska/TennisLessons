package io.github.akuszewska.tennislessons.builder;

import io.github.akuszewska.tennislessons.domain.User;

public class UserBuilder {

    public static final String INSTRUCTOR_USERNAME = "Instructor";
    public static final String INSTRUCTOR_PHONE = "567981234";
    public static final String STUDENT_USERNAME = "Student";
    public static final String STUDENT_PHONE = "123456789";

    public static User createDefaultInstructor() {

        return User.builder()
                .username(INSTRUCTOR_USERNAME)
                .phone(INSTRUCTOR_PHONE)
                .build();
    }

    public static User createDefaultStudent() {

        return User.builder()
                .username(STUDENT_USERNAME)
                .phone(STUDENT_PHONE)
                .build();
    }
}
