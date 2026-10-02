package it.linlo.dashbackend.autenticazione.security;

import it.linlo.dashbackend.autenticazione.domain.AppUser;
import it.linlo.dashbackend.autenticazione.domain.AppUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository users;

    public AppUserDetailsService(AppUserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser u = users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));
        return User.withUsername(u.getUsername())
                .password(u.getPasswordHash())
                .authorities(u.getRoles().stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.name())).toList())
                .build();
    }
}
