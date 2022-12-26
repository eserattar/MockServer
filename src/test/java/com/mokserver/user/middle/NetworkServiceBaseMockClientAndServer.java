package com.mokserver.user.middle;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.mockserver.model.Parameters;
import org.springframework.util.ResourceUtils;
import shaded_package.org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;

/**
 * @author Eser ATTAR
 */
public class NetworkServiceBaseMockClientAndServer {

  protected static final String RETURN_SINGLE_ITEM_SUCCESS_RESPONSE_JSON = "returnSingleItemSuccessResponse.json";
  protected static final String RETURN_MULTI_ITEMS_SUCCESS_RESPONSE_JSON = "returnMultiItemsSuccessResponse.json";
  protected static final String RETURN_5XX_FAILURE_RESPONSE_JSON = "return5xxFailureResponse.json";
  protected static final String RETURN_4XX_FAILURE_RESPONSE_JSON = "return4xxFailureResponse.json";

  protected static final Charset UTF_8 = Charset.forName("UTF-8");
  protected static final Integer PAGE_NUMBER = new Integer(2);
  protected static final String MALE = "male";
  protected static final String FEMALE = "female";
  protected static final String NATIONAL = "TR";
  protected static final String BASE_FILE_PATH_USERSERVICE = "classpath:mockserver/userservice/";

  static{
    List<String> turnOffLogList = Arrays.asList(
        "shaded_package",
        "io.netty",
        "reactor.netty"
    );
    LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
    turnOffLogList.stream().forEach(pgName ->  loggerContext.getLogger(pgName).setLevel(ch.qos.logback.classic.Level.OFF));
  }
  protected static ClientAndServer clientAndServer;
  @BeforeAll
  public static void startMockServer() {
    clientAndServer = new ClientAndServer();
  }

  @AfterAll
  public static void stopMockServer() {
    if (clientAndServer != null) {
      clientAndServer.stop();
    }
  }
  protected String readFileToString(String resourceLocation) throws Exception {
    return FileUtils.readFileToString(ResourceUtils.getFile(resourceLocation), UTF_8);
  }
  protected void setMockServerUpForGetUser(Parameters parameters, String fileName, int statusCode) throws Exception {
    // @formatter:off
    clientAndServer
        .when(
            HttpRequest.request()
                .withMethod("GET")
                .withQueryStringParameters(parameters))
        .respond(
            HttpResponse.response()
                .withStatusCode(statusCode)
                .withBody(readFileToString(BASE_FILE_PATH_USERSERVICE + fileName), MediaType.APPLICATION_JSON));
    // @formatter:on
  }

}
