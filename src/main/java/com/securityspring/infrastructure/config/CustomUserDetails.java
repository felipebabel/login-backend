package com.securityspring.infrastructure.config;

import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import com.securityspring.domain.model.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private String role;

    public CustomUserDetails(UserEntity user) {
        this.id = user.getIdentifier();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole().name();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
