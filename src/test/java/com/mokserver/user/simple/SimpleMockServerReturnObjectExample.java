package com.mokserver.user.simple;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author Eser ATTAR
 */
@Slf4j
@MockServerTest("server.url=http://localhost:${mockServerPort}")
@ExtendWith(SpringExtension.class)
public class SimpleMockServerReturnObjectExample {

  @Value("${server.url}")
  private String serverUrl;

  private MockServerClient mockServerClient;

  @Test
  void unitTestCase() {
    final String path = "/user/1/message";
    final String id = "1";
    final String name = "Eser";
    final String surname = "ATTAR";
    final String email = "eserattar@gmail.com";
    final String body = "{" +
        "   \"id\": " + id + "," +
        "   \"name\": \"" + name + "\"," +
        "   \"surname\": \"" + surname + "\"," +
        "   \"email\": \"" + email + "\"" +
        "}";

    //Send instruction to MockServer for the mock response of "/user/1/message" path via MockServerClient
    mockServerClient
        .when(HttpRequest.request().withMethod("GET").withPath(path))
        .respond(HttpResponse.response().withStatusCode(200)
            .withContentType(MediaType.APPLICATION_JSON)
            .withBody(body));

    // Trigger HTTP call to "/user/1/message" and assert the result
    WebTestClient webTestClient = WebTestClient.bindToServer().baseUrl(serverUrl).build();
    webTestClient.get()
        .uri(path)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.id").isEqualTo(id)
        .jsonPath("$.name").isEqualTo(name)
        .jsonPath("$.surname").isEqualTo(surname)
        .jsonPath("$.email").isEqualTo(email);
  }
}