package br.org.bandasantabarbara.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    security = @SecurityRequirement(name = "csrf")
)
@SecurityScheme(
    name = "csrf",
    type = SecuritySchemeType.APIKEY,
    in = SecuritySchemeIn.HEADER,
    paramName = "X-XSRF-TOKEN",
    description = "Cole o campo token retornado por GET /csrf"
)
public class SwaggerCsrfConfig {
}