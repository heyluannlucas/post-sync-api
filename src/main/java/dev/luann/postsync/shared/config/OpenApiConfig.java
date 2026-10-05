package dev.luann.postsync.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadados exibidos na documentação OpenAPI e no Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("Post Sync API")
                .version("1.0.0")
                .description("Sincroniza posts de uma API pública para o PostgreSQL e os expõe via REST."));
    }
}
