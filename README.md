# MockServer Example

## Overview
The following repo contains examples for MockServer.

## Guidelines
Run up the app by UserApplication.java.
To test MockServer examples goto test classes and run the class or test case one by one.
## Simple MockServer Example

Source code for the example is located in \src\test\java\com\mokserver\user\simple\SimpleMockServerExample.java.  The code demonstrate how to create a simple MockServer.
 
```java

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

```
This code just creates an instance of MockServerClient and calls the endpoint with WebTestClient.

Please see the middle and actions examples for advance.
## More Info
More information and API documentation can be found at https://www.mock-server.com/

## Disclaimers
* This is a starter example and intended to demonstrate how to use MockServer and implementation. There are potentially other ways to approach it and alternatives could be considered.

## License
Apache License 2.0
  

## Support
<br> Please contact me at eserattar@gmail.com