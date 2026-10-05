package dev.luann.postsync.post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;

/**
 * Post sincronizado da API pública. O identificador é o mesmo da origem.
 */
@Entity
@Table(name = "posts")
public class Post {

    @Id
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "synced_at", nullable = false)
    private Instant syncedAt;

    @Version
    private Long version;

    protected Post() {
    }

    public Post(Long id, Long userId, String title, String body, Instant syncedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.syncedAt = syncedAt;
    }

    /**
     * Atualiza os dados do post com o conteúdo mais recente da origem.
     */
    public void refresh(Long userId, String title, String body, Instant syncedAt) {
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.syncedAt = syncedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Instant getSyncedAt() {
        return syncedAt;
    }
}
