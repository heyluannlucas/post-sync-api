package dev.luann.postsync.post;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acesso a dados de {@link Post}.
 */
public interface PostRepository extends JpaRepository<Post, Long> {
}
