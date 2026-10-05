package dev.luann.postsync.post;

import dev.luann.postsync.external.ExternalPost;
import dev.luann.postsync.external.ExternalPostClient;
import dev.luann.postsync.post.dto.SyncResponse;
import dev.luann.postsync.shared.exception.PostNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-05T12:00:00Z");

    @Mock
    private PostRepository repository;

    @Mock
    private ExternalPostClient client;

    @Captor
    private ArgumentCaptor<List<Post>> savedPosts;

    private PostService service;

    @BeforeEach
    void setUp() {
        service = new PostService(repository, client, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void syncShouldInsertNewPostsAndRefreshExistingOnes() {
        Post existing = new Post(1L, 1L, "old title", "old body", Instant.EPOCH);
        when(client.fetchPosts()).thenReturn(List.of(
                new ExternalPost(1L, 1L, "new title", "new body"),
                new ExternalPost(2L, 7L, "title 2", "body 2")));
        when(repository.findAllById(List.of(1L, 2L))).thenReturn(List.of(existing));

        SyncResponse response = service.sync();

        verify(repository).saveAll(savedPosts.capture());
        assertThat(response.synced()).isEqualTo(2);
        assertThat(response.syncedAt()).isEqualTo(NOW);
        assertThat(savedPosts.getValue())
                .extracting(Post::getId, Post::getTitle, Post::getSyncedAt)
                .containsExactly(
                        tuple(1L, "new title", NOW),
                        tuple(2L, "title 2", NOW));
        assertThat(savedPosts.getValue().get(0)).isSameAs(existing);
    }

    @Test
    void findByIdShouldThrowWhenPostDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("99");
    }
}
