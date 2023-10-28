package io.github.akuszewska.tennislessons.auth.service;

import io.github.akuszewska.tennislessons.auth.dto.AuthenticationDTO;
import io.github.akuszewska.tennislessons.auth.dto.AuthenticationResponse;
import io.github.akuszewska.tennislessons.auth.dto.RegistrationRequest;
import io.github.akuszewska.tennislessons.domain.Role;
import io.github.akuszewska.tennislessons.domain.User;
import io.github.akuszewska.tennislessons.domain.dictionary.RoleEnum;
import io.github.akuszewska.tennislessons.repository.RoleRepository;
import io.github.akuszewska.tennislessons.repository.UserRepository;
import io.github.akuszewska.tennislessons.security.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static io.github.akuszewska.tennislessons.security.dictionary.Message.ROLE_NOT_FOUND;
import static io.github.akuszewska.tennislessons.security.dictionary.Message.USERNAME_EXISTS;
import static io.github.akuszewska.tennislessons.security.dictionary.Message.USERNAME_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse register(RegistrationRequest request) {

        String username = request.getUsername();

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException(USERNAME_EXISTS);
        }

        Role role;
        if (request.getIsInstructor()) {
            role = roleRepository.findByName(RoleEnum.ROLE_INSTRUCTOR)
                    .orElseThrow(() -> new AuthenticationServiceException(ROLE_NOT_FOUND));
        } else {
            role = roleRepository.findByName(RoleEnum.ROLE_STUDENT)
                    .orElseThrow(() -> new AuthenticationServiceException(ROLE_NOT_FOUND));
        }

        User newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .phone(request.getPhone())
                .build();
        userRepository.save(newUser);

        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateToken(newUser))
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND));

        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .build();
    }
}
