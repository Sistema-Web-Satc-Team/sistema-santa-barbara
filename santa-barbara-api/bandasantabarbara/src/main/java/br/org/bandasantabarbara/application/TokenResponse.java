package br.org.bandasantabarbara.application;

public record TokenResponse(
        String token,
        String tipo,
        long expiraEmEmSegundos
) {
    public static TokenResponse bearer(String token, long expiraEmEmSegundos) {
        return new TokenResponse(token, "Bearer", expiraEmEmSegundos);
    }
}