package br.org.bandasantabarbara.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DatabaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deveConectarAoPostgresDeTeste() {
        Integer resultado = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertThat(resultado).isEqualTo(1);

        String banco = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
        assertThat(banco).isEqualTo("postgres_test");
    }

    @Test
    void deveTerAplicadoAsMigrations() {
        Integer aplicadas = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true",
                Integer.class);
        assertThat(aplicadas).isGreaterThan(0);
    }
}