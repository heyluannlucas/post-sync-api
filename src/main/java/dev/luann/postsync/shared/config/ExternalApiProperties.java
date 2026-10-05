package dev.luann.postsync.shared.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Parâmetros de acesso à API pública consumida na sincronização.
 *
 * @param baseUrl        endereço base da API externa
 * @param connectTimeout tempo máximo para estabelecer a conexão
 * @param readTimeout    tempo máximo de espera pela resposta
 */
@Validated
@ConfigurationProperties(prefix = "app.external-api")
public record ExternalApiProperties(
        @NotBlank String baseUrl,
        @NotNull Duration connectTimeout,
        @NotNull Duration readTimeout
) {
}
