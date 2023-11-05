package io.github.akuszewska.tennislessons.service;

import io.github.akuszewska.tennislessons.domain.User;

public interface AuthService {

    User getCurrentLoggedInUser();
}
