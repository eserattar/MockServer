package com.mokserver.user.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

/**
 * @author Eser ATTAR
 */

@Slf4j
@Configuration
public class WebClientConfig {

  private static final int MAX_IN_MEMORY = 16 * 1024 * 1024;
  private static final int TIMEOUT = 60_000;

  private final ApiProperties apiProperties;

  public WebClientConfig(ApiProperties apiProperties) {
    this.apiProperties = apiProperties;
  }

  @Bean
  public WebClient webClient(WebClient.Builder builder) {

    HttpClient httpClient = httpClient();

    return builder
        .baseUrl(apiProperties.getBaseUrl())
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .exchangeStrategies(ExchangeStrategies.builder()
            .codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(MAX_IN_MEMORY))
            .build())
        .filter(ExchangeFilterFunction.ofRequestProcessor(this::logRequest))
        .filter(ExchangeFilterFunction.ofResponseProcessor(this::logResponse))
        .build();

  }

  private HttpClient httpClient() {

    return HttpClient.create()
        .wiretap("logger", LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL)
        .compress(true)
        .keepAlive(true)
        .secure(spec -> spec.sslContext(SslContextBuilder.forClient()))
        .protocol(HttpProtocol.HTTP11, HttpProtocol.H2)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
        .doOnConnected(this::doOnConnected);

  }

  private void doOnConnected(Connection conn) {
    conn.addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS))
        .addHandlerLast(new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
  }

  @Bean
  public ReactorResourceFactory reactorResourceFactory() {
    return new ReactorResourceFactory();
  }

  private Mono<ClientRequest> logRequest(ClientRequest request) {
    log.info("{}Request: {} {}", request.logPrefix(), request.method(), request.url());
    log.info("{}--- Http Headers: ---", request.logPrefix());
    request.headers().forEach((key, values) -> logElement(request.logPrefix(), key, values));
    log.info("{}--- Http Cookies: ---", request.logPrefix());
    request.cookies().forEach((key, values) -> logElement(request.logPrefix(), key, values));
    return Mono.just(request);
  }

  private Mono<ClientResponse> logResponse(ClientResponse response) {
    log.info("{}Response: {}", response.logPrefix(), response.statusCode());
    response.headers().asHttpHeaders()
        .forEach((key, values) -> logElement(response.logPrefix(), key, values));
    return Mono.just(response);
  }

  private void logElement(String logPrefix, String key, List<String> values) {
    values.forEach(value -> log.info("{}{}={}", logPrefix, key, value));
  }

}

