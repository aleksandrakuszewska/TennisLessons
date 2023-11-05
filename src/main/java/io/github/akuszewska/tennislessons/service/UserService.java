package io.github.akuszewska.tennislessons.service;

import io.github.akuszewska.tennislessons.dto.ProfileDTO;

public interface UserService {

    ProfileDTO getProfile();
    ProfileDTO getProfileById(Long id);
}
