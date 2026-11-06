package br.org.bandasantabarbara.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Santa Barbara API",
                version = "v1",
                description = "API do sistema Santa Bárbara."
        )
)
public class OpenApiConfig {
}
