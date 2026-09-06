package br.org.bandasantabarbara.infrastructure.security;

import br.org.bandasantabarbara.model.Membro;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final long expiracaoSegundos;

    public TokenService(
            JwtEncoder jwtEncoder,
            @Value("${api.security.token.expiration-seconds:7200}") long expiracaoSegundos) {
        this.jwtEncoder = jwtEncoder;
        this.expiracaoSegundos = expiracaoSegundos;
    }

    public String gerarToken(Membro membro) {
        var agora = Instant.now();

        var claims = JwtClaimsSet.builder()
                .issuer("santa-barbara-api")
                .issuedAt(agora)
                .expiresAt(agora.plusSeconds(expiracaoSegundos))
                .subject(membro.getId().toString())
                .claim("email", membro.getEmail())
                .claim("nomeUsuario", membro.getNomeDeUsuario())
                .build();

        var header = JwsHeader.with(SignatureAlgorithm.RS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long getTempoExpiracaoSegundos() {
        return expiracaoSegundos;
    }
}