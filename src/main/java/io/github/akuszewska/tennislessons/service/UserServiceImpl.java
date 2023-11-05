package io.github.akuszewska.tennislessons.service;

import io.github.akuszewska.tennislessons.domain.User;
import io.github.akuszewska.tennislessons.dto.ProfileDTO;
import io.github.akuszewska.tennislessons.mapper.ProfileDTOMapper;
import io.github.akuszewska.tennislessons.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.github.akuszewska.tennislessons.exception.ErrorMessage.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final ProfileDTOMapper profileMapper;

    @Override
    public ProfileDTO getProfile() {

        User currentUser = authService.getCurrentLoggedInUser();
        return profileMapper.map(currentUser);
    }

    @Override
    public ProfileDTO getProfileById(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        return profileMapper.map(user);
    }
}
