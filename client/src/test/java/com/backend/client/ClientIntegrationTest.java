package com.backend.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.backend.client.model.dto.ClientDto;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT,
    properties={"spring.security.enabled=false"}
)
@ActiveProfiles("test")
public class ClientIntegrationTest {

    WebTestClient client;

    @BeforeEach
    void setUp(ApplicationContext context) {
        client = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    @Order(1)
    void testCreateClient() {

        ClientDto request = new ClientDto(null, "1712345677", "Jose Lema", "1234", "Masculino", 30,
                "Otavalo sn y principal", "098254785", true);

        client.post().uri("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ClientDto.class)
                .consumeWith(response -> {
                    ClientDto clientResponse = response.getResponseBody();
                    assertNotNull(clientResponse);
                    assertEquals(1L, clientResponse.getId());
                    assertEquals("Jose Lema", clientResponse.getName());
                });

    }
}
