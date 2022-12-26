package com.mokserver.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Eser ATTAR
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "user")
public class ApiProperties {

  private String baseUrl;
}
