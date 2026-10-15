package br.org.bandasantabarbara.infrastructure.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JwtPapelConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<String> papeis = jwt.getClaimAsStringList("papeis");

        if (papeis == null || papeis.isEmpty()) {
            return Collections.emptyList();
        }

        return papeis.stream()
                .map(papel -> (GrantedAuthority) new SimpleGrantedAuthority(papel))
                .toList();
    }
}