package br.org.bandasantabarbara.infrastructure.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JwtPapelConverter implements Converter<Jwt, GrantedAuthority> {

    @Override
    public GrantedAuthority convert(Jwt jwt) {
        String papel = jwt.getClaimAsString("permissoes");

        if (papel == null || papel.isEmpty()) {
            return new SimpleGrantedAuthority();
        }

        return permissoes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}