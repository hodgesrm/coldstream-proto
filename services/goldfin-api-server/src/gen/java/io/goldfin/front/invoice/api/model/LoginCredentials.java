/*
 * Goldfin Invoice Processing API
 * Goldfin Invoice Analysis API
 *
 * OpenAPI spec version: 1.0.0
 * Contact: rhodges@skylineresearch.comm
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.goldfin.front.invoice.api.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;

/**
 * Login credentials
 */
@ApiModel(description = "Login credentials")

public class LoginCredentials   {
  @JsonProperty("user")
  private String user = null;

  @JsonProperty("password")
  private String password = null;

  public LoginCredentials user(String user) {
    this.user = user;
    return this;
  }

  /**
   * User name
   * @return user
   **/
  @JsonProperty("user")
  @ApiModelProperty(value = "User name")
  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public LoginCredentials password(String password) {
    this.password = password;
    return this;
  }

  /**
   * User password
   * @return password
   **/
  @JsonProperty("password")
  @ApiModelProperty(value = "User password")
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LoginCredentials loginCredentials = (LoginCredentials) o;
    return Objects.equals(this.user, loginCredentials.user) &&
        Objects.equals(this.password, loginCredentials.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, password);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LoginCredentials {\n");
    
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
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
