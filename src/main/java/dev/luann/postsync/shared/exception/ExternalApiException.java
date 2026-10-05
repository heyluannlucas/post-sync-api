package dev.luann.postsync.shared.exception;

/**
 * Lançada quando a API pública está indisponível ou responde de forma inválida.
 */
public class ExternalApiException extends RuntimeException {

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
