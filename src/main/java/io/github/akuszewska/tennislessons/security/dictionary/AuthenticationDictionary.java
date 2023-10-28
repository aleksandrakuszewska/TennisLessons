package io.github.akuszewska.tennislessons.security.dictionary;

public interface AuthenticationDictionary {

    String INSTRUCTOR_PERMISSION = "hasRole('ROLE_INSTRUCTOR')";
    String STUDENT_PERMISSION = "hasRole('ROLE_STUDENT')";
}
