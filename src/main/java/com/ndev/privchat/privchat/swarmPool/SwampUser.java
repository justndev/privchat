package com.ndev.privchat.privchat.swarmPool;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
public class SwampUser implements UserDetails {
    private String nickname;
    private String password;
    private Date createdAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return "{noop}" + nickname;
    }

    @Override
    public String getUsername() {
        return nickname;
    }
}
