package dev.luann.postsync.post;

import dev.luann.postsync.post.dto.PostResponse;
import dev.luann.postsync.post.dto.SyncResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de sincronização e consulta de posts.
 */
@RestController
@Tag(name = "Posts", description = "Sincronização e consulta de posts")
public class PostController {

    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @PostMapping("/sync")
    @Operation(summary = "Sincroniza os posts da API pública para o banco")
    @ApiResponse(responseCode = "200", description = "Sincronização concluída")
    @ApiResponse(responseCode = "502", description = "API externa indisponível",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    public SyncResponse sync() {
        return service.sync();
    }

    @GetMapping("/posts")
    @Operation(summary = "Lista os posts de forma paginada")
    @ApiResponse(responseCode = "200", description = "Página de posts")
    public Page<PostResponse> findAll(@ParameterObject Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/posts/{id}")
    @Operation(summary = "Consulta um post pelo identificador")
    @ApiResponse(responseCode = "200", description = "Post encontrado")
    @ApiResponse(responseCode = "404", description = "Post não encontrado",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE))
    public PostResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }
}
