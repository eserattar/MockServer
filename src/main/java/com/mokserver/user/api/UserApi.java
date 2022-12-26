package com.mokserver.user.api;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Eser ATTAR
 */
@Validated
public interface UserApi {

  @GetMapping(value = "/", produces = {"application/json"})
  ResponseEntity<String> getUser(
      @NotNull @Valid @RequestParam(value = "national", required = true) String national,
      @NotNull @Valid @RequestParam(value = "gender", required = true) String gender,
      @NotNull @Valid @Range(min = 1, max = 10) @RequestParam(value = "pageNum", required = true) Integer pageNum);

}
