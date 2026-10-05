package dev.luann.postsync.post;

import dev.luann.postsync.external.ExternalPost;
import dev.luann.postsync.external.ExternalPostClient;
import dev.luann.postsync.shared.exception.ExternalApiException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class PostApiIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExternalPostClient client;

    @Test
    void syncShouldPersistPostsAndExposeThemThroughTheApi() throws Exception {
        when(client.fetchPosts()).thenReturn(List.of(
                new ExternalPost(1L, 1L, "title 1", "body 1"),
                new ExternalPost(2L, 2L, "title 2", "body 2")));

        mockMvc.perform(post("/sync"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.synced").value(2));

        mockMvc.perform(post("/sync"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.synced").value(2));

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page.totalElements").value(2));

        mockMvc.perform(get("/posts/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title 2"));
    }

    @Test
    void syncShouldReturnBadGatewayWhenExternalApiFails() throws Exception {
        when(client.fetchPosts()).thenThrow(new ExternalApiException("falha", new RuntimeException()));

        mockMvc.perform(post("/sync"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.title").value("API externa indisponível"));
    }

    @Test
    void healthShouldReportUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
