package io.github.akuszewska.tennislessons.domain.dictionary;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {

    ROLE_INSTRUCTOR("ROLE_INSTRUCTOR"),
    ROLE_STUDENT("ROLE_STUDENT");

    private final String name;
}
