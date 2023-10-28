package io.github.akuszewska.tennislessons.security.dictionary;

public interface Message {

    String USERNAME_NOT_FOUND = "User with given username does not exists!";
    String USERNAME_EXISTS = "User with given username already exists";
    String ROLE_NOT_FOUND = "Role does not exists in database";
}
