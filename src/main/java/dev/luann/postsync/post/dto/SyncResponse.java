package dev.luann.postsync.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Resultado de uma execução de sincronização.
 */
@Schema(description = "Resumo da sincronização")
public record SyncResponse(
        @Schema(description = "Quantidade de posts sincronizados", example = "100") int synced,
        @Schema(description = "Momento em que a sincronização foi executada") Instant syncedAt
) {
}
