/*
 * Goldfin Service API
 * REST API for Goldfin Intelligent Invoice Processing
 *
 * OpenAPI spec version: 1.0.0
 * Contact: info@goldfin.io
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.goldfin.admin.service.api.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * API key pair
 */
@ApiModel(description = "API key pair")

public class ApiKey   {
  @JsonProperty("id")
  private UUID id = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("userId")
  private UUID userId = null;

  @JsonProperty("secret")
  private String secret = null;

  @JsonProperty("lastTouchedDate")
  private Date lastTouchedDate = null;

  public ApiKey id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Unique API key ID
   * @return id
   **/
  @JsonProperty("id")
  @ApiModelProperty(value = "Unique API key ID")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public ApiKey name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of this API key; must be unique among keys owned by a user.
   * @return name
   **/
  @JsonProperty("name")
  @ApiModelProperty(value = "Name of this API key; must be unique among keys owned by a user.")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ApiKey userId(UUID userId) {
    this.userId = userId;
    return this;
  }

  /**
   * User ID
   * @return userId
   **/
  @JsonProperty("userId")
  @ApiModelProperty(value = "User ID")
  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public ApiKey secret(String secret) {
    this.secret = secret;
    return this;
  }

  /**
   * Secret password for the API key. This is only shown on initial creation; if lost a new API key must be created to replace it.
   * @return secret
   **/
  @JsonProperty("secret")
  @ApiModelProperty(value = "Secret password for the API key. This is only shown on initial creation; if lost a new API key must be created to replace it.")
  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public ApiKey lastTouchedDate(Date lastTouchedDate) {
    this.lastTouchedDate = lastTouchedDate;
    return this;
  }

  /**
   * Date of last use of key
   * @return lastTouchedDate
   **/
  @JsonProperty("lastTouchedDate")
  @ApiModelProperty(value = "Date of last use of key")
  public Date getLastTouchedDate() {
    return lastTouchedDate;
  }

  public void setLastTouchedDate(Date lastTouchedDate) {
    this.lastTouchedDate = lastTouchedDate;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApiKey apiKey = (ApiKey) o;
    return Objects.equals(this.id, apiKey.id) &&
        Objects.equals(this.name, apiKey.name) &&
        Objects.equals(this.userId, apiKey.userId) &&
        Objects.equals(this.secret, apiKey.secret) &&
        Objects.equals(this.lastTouchedDate, apiKey.lastTouchedDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, userId, secret, lastTouchedDate);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiKey {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    secret: ").append(toIndentedString(secret)).append("\n");
    sb.append("    lastTouchedDate: ").append(toIndentedString(lastTouchedDate)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

