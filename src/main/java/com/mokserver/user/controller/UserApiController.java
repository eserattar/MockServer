package com.mokserver.user.controller;

import com.mokserver.user.api.UserApi;
import com.mokserver.user.service.UserService;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Eser ATTAR
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserApiController implements UserApi {
  private final UserService userService;
  @GetMapping(value = "/", produces = {"application/json"})
  public ResponseEntity<String> getUser(
      @RequestParam(value = "national", required = true) @NotNull @Valid String national,
      @RequestParam(value = "gender", required = true) @NotNull @Valid String gender,
      @RequestParam(required = false, name = "pageNum")  @Range(min = 1, max = 5) @NotNull @Valid Integer pageNum) {
    String reqResDetails = userService.getUser(national, gender, pageNum);
    return new ResponseEntity<>(reqResDetails, HttpStatus.OK);
  }
}
