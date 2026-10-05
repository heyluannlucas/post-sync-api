package dev.luann.postsync.post;

import dev.luann.postsync.post.dto.PostResponse;
import dev.luann.postsync.shared.exception.PostNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService service;

    @Test
    void findByIdShouldReturnPost() throws Exception {
        when(service.findById(1L)).thenReturn(
                new PostResponse(1L, 1L, "title", "body", Instant.parse("2026-10-05T12:00:00Z")));

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("title"));
    }

    @Test
    void findByIdShouldReturnProblemDetailWhenPostDoesNotExist() throws Exception {
        when(service.findById(99L)).thenThrow(new PostNotFoundException(99L));

        mockMvc.perform(get("/posts/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Post não encontrado"));
    }

    @Test
    void findByIdShouldReturnBadRequestWhenIdIsNotNumeric() throws Exception {
        mockMvc.perform(get("/posts/abc"))
                .andExpect(status().isBadRequest());
    }
}
