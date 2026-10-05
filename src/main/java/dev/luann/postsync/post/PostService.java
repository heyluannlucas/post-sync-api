package dev.luann.postsync.post;

import dev.luann.postsync.external.ExternalPost;
import dev.luann.postsync.external.ExternalPostClient;
import dev.luann.postsync.post.dto.PostResponse;
import dev.luann.postsync.post.dto.SyncResponse;
import dev.luann.postsync.shared.exception.PostNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Regras de negócio de sincronização e consulta de posts.
 */
@Service
public class PostService {

    private final PostRepository repository;
    private final ExternalPostClient client;
    private final Clock clock;

    public PostService(PostRepository repository, ExternalPostClient client, Clock clock) {
        this.repository = repository;
        this.client = client;
        this.clock = clock;
    }

    /**
     * Importa os posts da API pública, inserindo os novos e atualizando os existentes
     * com uma única consulta de leitura e gravação em lote.
     */
    @Transactional
    public SyncResponse sync() {
        List<ExternalPost> external = client.fetchPosts();
        Instant now = clock.instant();

        Map<Long, Post> existing = repository
                .findAllById(external.stream().map(ExternalPost::id).toList())
                .stream()
                .collect(Collectors.toMap(Post::getId, Function.identity()));

        List<Post> posts = external.stream()
                .map(source -> upsert(existing.get(source.id()), source, now))
                .toList();

        repository.saveAll(posts);
        return new SyncResponse(posts.size(), now);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(PostResponse::from);
    }

    /**
     * @throws PostNotFoundException se o post não existir
     */
    @Transactional(readOnly = true)
    public PostResponse findById(Long id) {
        return repository.findById(id)
                .map(PostResponse::from)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    private Post upsert(Post current, ExternalPost source, Instant syncedAt) {
        if (current == null) {
            return new Post(source.id(), source.userId(), source.title(), source.body(), syncedAt);
        }
        current.refresh(source.userId(), source.title(), source.body(), syncedAt);
        return current;
    }
}
