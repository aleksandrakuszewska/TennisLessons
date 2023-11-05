package io.github.akuszewska.tennislessons.dto;

import io.github.akuszewska.tennislessons.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {

    private Long userId;
    private String username;
    private String phone;
    private RoleDTO role;

}
