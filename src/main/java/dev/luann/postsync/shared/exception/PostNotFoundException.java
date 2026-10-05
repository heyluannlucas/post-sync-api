package dev.luann.postsync.shared.exception;

/**
 * Lançada quando nenhum post existe para o identificador informado.
 */
public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(Long id) {
        super("Post não encontrado: " + id);
    }
}
