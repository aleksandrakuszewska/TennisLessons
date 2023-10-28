package io.github.akuszewska.tennislessons.auth.controller;

import io.github.akuszewska.tennislessons.auth.dto.AuthenticationDTO;
import io.github.akuszewska.tennislessons.auth.dto.AuthenticationResponse;
import io.github.akuszewska.tennislessons.auth.dto.RegistrationRequest;
import io.github.akuszewska.tennislessons.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    // wstrzyknięcie zależności przez konstruktor
    private final AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationDTO request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
