package it.linlo.dashbackend.autenticazione.service;

import it.linlo.dashbackend.autenticazione.domain.AppUser;
import it.linlo.dashbackend.autenticazione.domain.AppUserRepository;
import it.linlo.dashbackend.autenticazione.domain.Role;
import it.linlo.dashbackend.autenticazione.dto.AuthResponse;
import it.linlo.dashbackend.autenticazione.dto.LoginRequest;
import it.linlo.dashbackend.autenticazione.dto.RegisterRequest;
import it.linlo.dashbackend.autenticazione.security.JwtService;
import it.linlo.dashbackend.core.error.ConflictException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthService {

    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AppUserRepository users, PasswordEncoder encoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.users = users;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (users.existsByUsername(request.username())) {
            throw new ConflictException("auth.username.taken");
        }
        if (users.existsByEmail(request.email())) {
            throw new ConflictException("auth.email.registered");
        }
        AppUser user = users.save(new AppUser(
                request.username(), request.email(), encoder.encode(request.password()), Set.of(Role.USER)));
        return login(new LoginRequest(user.getUsername(), request.password()));
    }

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String token = jwtService.generateToken((UserDetails) auth.getPrincipal());
        return AuthResponse.bearer(token, jwtService.getExpirationSeconds());
    }
}
