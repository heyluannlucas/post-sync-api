package dev.luann.postsync.post.dto;

import dev.luann.postsync.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Post exposto pela API.
 */
@Schema(description = "Post sincronizado da API pública")
public record PostResponse(
        @Schema(description = "Identificador do post na origem", example = "1") Long id,
        @Schema(description = "Identificador do autor", example = "1") Long userId,
        @Schema(description = "Título do post", example = "sunt aut facere repellat provident") String title,
        @Schema(description = "Conteúdo do post") String body,
        @Schema(description = "Momento da última sincronização") Instant syncedAt
) {

    public static PostResponse from(Post post) {
        return new PostResponse(post.getId(), post.getUserId(), post.getTitle(), post.getBody(), post.getSyncedAt());
    }
}
