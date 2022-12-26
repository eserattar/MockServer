package com.mokserver.user.actions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.BodyInserters;

/**
 * @author Eser ATTAR
 */
@MockServerTest("endpoint:http://localhost:${mockServerPort}")
@ExtendWith(SpringExtension.class)
public class StatusCodeExample {

  @Value("${endpoint}")
  private String endpoint;

  private MockServerClient mockServerClient;

  @Test
  void testUserServiceWith5xxResponse() {
    String path = "/user";
    String body = "body";
    mockServerClient
        .when(HttpRequest.request().withMethod(RequestMethod.POST.name()).withPath(path)
            .withBody(body))
        .respond(HttpResponse.response().withStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

    WebTestClient webTestClient = WebTestClient.bindToServer().baseUrl(endpoint).build();
    webTestClient.post().uri(path).body(BodyInserters.fromValue(body)).exchange()
        .expectStatus().is5xxServerError();

  }

}
