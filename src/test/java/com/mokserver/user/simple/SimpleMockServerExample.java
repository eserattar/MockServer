package com.mokserver.user.simple;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.BodySpec;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Eser ATTAR
 */
@Slf4j
@MockServerTest("server.url=http://localhost:${mockServerPort}")
@ExtendWith(SpringExtension.class)
public class SimpleMockServerExample {

  @Value("${server.url}")
  private String serverUrl;
  private MockServerClient mockServerClient;

  @Test
  void unitTestCase() {
    String body = "simpleMockServerExample response part inside withBody";
    String path = "/user/1/message";

    //Send instruction to MockServer for the mock response of "/user/1/message" path via MockServerClient
    mockServerClient
        .when(HttpRequest.request().withMethod(RequestMethod.GET.name()).withPath(path))
        .respond(HttpResponse.response().withStatusCode(200)
            .withContentType(MediaType.APPLICATION_JSON)
            .withBody(body));

    //Trigger HTTP call to "/user/1/message" and assert the result
    WebTestClient webTestClient = WebTestClient.bindToServer().baseUrl(serverUrl).build();
    BodySpec<String, ?> response = webTestClient.get()
        .uri(path)
        .exchange()
        .expectStatus().isOk()
        .expectBody(String.class);

    Assertions.assertNotNull(response);
    Assertions.assertNotNull(response.returnResult());
    Assertions.assertNotNull(response.returnResult().getResponseBody());
    Assertions.assertNotNull(response.returnResult().getResponseBody(), body);
  }
}
