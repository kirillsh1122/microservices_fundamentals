package com.microservice.architecture.overview.auth_server.model;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Data
@Entity
@Table(name = "users")
@RequiredArgsConstructor
@NoArgsConstructor(force=true, access=AccessLevel.PRIVATE)
public class User implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private final String username;
    private final String password;
    private final String role;

    private static final HashMap<String, Collection<? extends GrantedAuthority>> roleAuthorityMapping =
            new HashMap<>() {{
                put("ROLE_ADMIN",
                        List.of(
                                new SimpleGrantedAuthority("readStorageEntries"),
                                new SimpleGrantedAuthority("createStorageEntries"),
                                new SimpleGrantedAuthority("deleteStorageEntries")
                        ));
                put("ROLE_GUEST",
                        List.of(
                                new SimpleGrantedAuthority("readStorageEntries")
                        ));
            }};

    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleAuthorityMapping.getOrDefault(role, Collections.emptyList());
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isEnabled() {
        return true;
    }
}
