package dev.luann.postsync.external;

/**
 * Representação de um post conforme retornado pela API pública.
 */
public record ExternalPost(Long id, Long userId, String title, String body) {
}
