package io.github.akuszewska.tennislessons.auth.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RegistrationRequest extends AuthenticationDTO {

    private Boolean isInstructor = false;
    private String phone;
}
