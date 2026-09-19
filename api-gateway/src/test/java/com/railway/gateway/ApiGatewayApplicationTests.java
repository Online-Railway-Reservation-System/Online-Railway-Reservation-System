package com.railway.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class ApiGatewayApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void contextLoads() {
    }

    @Test
    void testSwaggerUiEndpoints() {
        webTestClient.get()
                .uri("/swagger-ui.html")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", loc -> {
                    System.out.println("SWAGGER_UI_REDIRECT_LOCATION: " + loc);
                });

        webTestClient.get()
                .uri("/webjars/swagger-ui/index.html")
                .exchange()
                .expectStatus().isOk();
    }
}