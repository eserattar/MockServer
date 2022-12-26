package com.mokserver.user.service;

import com.mokserver.user.config.ApiProperties;
import com.mokserver.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * @author Eser ATTAR
 */
@Slf4j
@Service("userService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

  private final WebClient webClient;
  private final ApiProperties apiProperties;

  @Override
  public String getUser(String national, String gender, Integer pageNum) {
    return userWs(national, gender, pageNum).block();
  }

  private Mono<String> userWs(String national, String gender, Integer pageNum) {
    try {
      String uri = apiProperties.getBaseUrl() + "?pageNum={pageNum}&nat={national}&gender={gender}";

      return webClient.get()
          .uri(uri, pageNum, national, gender)
          .retrieve()
          .onStatus(HttpStatus::isError, this::handleGetReqResDetailsError)
          .bodyToMono(String.class);
    } catch (WebClientResponseException we) {
      throw new UserException(
          "response status code: " + we.getStatusCode() + " message: " + we.getMessage());
    }
  }

  private Mono<? extends Throwable> handleGetReqResDetailsError(ClientResponse response) {
    return response.bodyToMono(String.class).flatMap(body -> Mono.error(
        new UserException(response.statusCode() + " could not get data from reqres.")));
  }

}
