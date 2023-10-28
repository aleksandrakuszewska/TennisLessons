package io.github.akuszewska.tennislessons.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class AuthenticationDTO {

    private String username;
    private String password;
}
