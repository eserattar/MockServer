package com.mokserver.user.actions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpClassCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Eser ATTAR
 */
@MockServerTest("server.uri:http://localhost:${mockServerPort}")
@ExtendWith(SpringExtension.class)
public class CallbackExample {

  private static String CALLBACK_CLASS_RUN_AND_DONE = "callback class run and done";
  @Value("${server.uri}")
  private String serverUri;

  private MockServerClient mockServerClient;

  @Test
  void testCallbackAction() {
    String path = "/user/56/details";
    mockServerClient.when(HttpRequest.request().withMethod(RequestMethod.GET.name()).withPath(path))
        .respond(HttpClassCallback.callback(ExampleCallbackClass.class));

    WebTestClient webTestClient = WebTestClient.bindToServer().baseUrl(serverUri).build();
    webTestClient.get().uri(path).exchange().expectStatus().isOk()
        .expectBody(String.class).isEqualTo(CALLBACK_CLASS_RUN_AND_DONE);

  }

  public static class ExampleCallbackClass implements
      ExpectationResponseCallback {

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
      return HttpResponse.response().withStatusCode(HttpStatus.OK.value()).withBody(
          CALLBACK_CLASS_RUN_AND_DONE);
    }
  }
}
