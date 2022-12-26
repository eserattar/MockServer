package com.mokserver.user.middle;

import com.mokserver.user.config.ApiProperties;
import com.mokserver.user.service.UserService;
import com.mokserver.user.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.model.Parameter;
import org.mockserver.model.Parameters;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Eser ATTAR
 */
@Slf4j
@ExtendWith(SpringExtension.class)
public class MockServerExistingServiceExample extends NetworkServiceBaseMockClientAndServer {

  private final ApiProperties apiProperties = new ApiProperties();
  private UserService userService;


  @BeforeEach
  void beforeEach() {
    setApiPropertiesUp();
    setUserServiceUp();
  }

  private void setApiPropertiesUp() {
    apiProperties.setBaseUrl(
        String.format("http://localhost:%s/api/users", clientAndServer.getLocalPort()));
  }

  private void setUserServiceUp() {
    WebClient webClient = WebClient.builder().baseUrl(apiProperties.getBaseUrl()).build();
    userService = new UserServiceImpl(webClient, apiProperties);
  }

  @Test
  void testGetUseForSingleUserResponse_Success() throws Exception {
    Parameters parameters = getParameters(FEMALE);
    setMockServerUpForGetUser(parameters, RETURN_SINGLE_ITEM_SUCCESS_RESPONSE_JSON,
        HttpStatus.OK.value());

    String actualResponse = userService.getUser(NATIONAL, FEMALE, PAGE_NUMBER);
    String expected = readFileToString(
        BASE_FILE_PATH_USERSERVICE + RETURN_SINGLE_ITEM_SUCCESS_RESPONSE_JSON);

    Assertions.assertNotNull(actualResponse);
    JSONAssert.assertEquals(expected, actualResponse, true);
  }

  @Test
  void testGetUseForMultipleUserResponse_Success() throws Exception {
    Parameters parameters = getParameters(MALE);
    setMockServerUpForGetUser(parameters, RETURN_MULTI_ITEMS_SUCCESS_RESPONSE_JSON,
        HttpStatus.OK.value());

    String actualResponse = userService.getUser(NATIONAL, MALE, PAGE_NUMBER);
    String expected = readFileToString(
        BASE_FILE_PATH_USERSERVICE + RETURN_MULTI_ITEMS_SUCCESS_RESPONSE_JSON);

    Assertions.assertNotNull(actualResponse);
    JSONAssert.assertEquals(expected, actualResponse, true);
  }

  private Parameters getParameters(String gender) {
    Parameter pageNum = new Parameter("pageNum", String.valueOf(PAGE_NUMBER));
    Parameter genderParam = new Parameter("gender", gender);
    Parameter nat = new Parameter("nat", NATIONAL);
    Parameters parameters = new Parameters(nat, genderParam, pageNum);
    return parameters;
  }

}
