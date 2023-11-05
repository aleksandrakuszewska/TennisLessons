package io.github.akuszewska.tennislessons.mapper;

import io.github.akuszewska.tennislessons.domain.Role;
import io.github.akuszewska.tennislessons.domain.User;
import io.github.akuszewska.tennislessons.dto.ProfileDTO;
import io.github.akuszewska.tennislessons.dto.RoleDTO;
import org.springframework.stereotype.Component;

@Component
public class ProfileDTOMapper {

    public ProfileDTO map(User from) {

        return ProfileDTO.builder()
                .userId(from.getId())
                .username(from.getUsername())
                .role(mapRoleToDTO(from.getRole()))
                .phone(from.getPhone())
                .build();
    }

    private RoleDTO mapRoleToDTO(Role from) {
        return RoleDTO.builder()
                .roleId(from.getId())
                .roleName(from.getName().getName())
                .build();
    }
}
