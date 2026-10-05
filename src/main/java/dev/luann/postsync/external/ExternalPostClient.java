package dev.luann.postsync.external;

import dev.luann.postsync.shared.exception.ExternalApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * Cliente HTTP da API pública de posts.
 */
@Component
public class ExternalPostClient {

    private final RestClient restClient;

    public ExternalPostClient(RestClient externalApiRestClient) {
        this.restClient = externalApiRestClient;
    }

    /**
     * Busca todos os posts disponíveis na API pública.
     *
     * @return lista de posts, vazia se a API não retornar conteúdo
     * @throws ExternalApiException se a API estiver indisponível ou responder com erro
     */
    public List<ExternalPost> fetchPosts() {
        try {
            ExternalPost[] posts = restClient.get()
                    .uri("/posts")
                    .retrieve()
                    .body(ExternalPost[].class);
            return posts == null ? List.of() : List.of(posts);
        } catch (RestClientException ex) {
            throw new ExternalApiException("Não foi possível obter os posts da API externa", ex);
        }
    }
}
