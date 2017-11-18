/*
 * Goldfin Invoice Processing API
 * Goldfin Invoice Analysis
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
 * Parameters for creating or updating a user account
 */
@ApiModel(description = "Parameters for creating or updating a user account")

public class UserParameters   {
  @JsonProperty("username")
  private String username = null;

  @JsonProperty("password")
  private String password = null;

  public UserParameters username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Unique user name
   * @return username
   **/
  @JsonProperty("username")
  @ApiModelProperty(value = "Unique user name")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserParameters password(String password) {
    this.password = password;
    return this;
  }

  /**
   * Password for user
   * @return password
   **/
  @JsonProperty("password")
  @ApiModelProperty(value = "Password for user")
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
    UserParameters userParameters = (UserParameters) o;
    return Objects.equals(this.username, userParameters.username) &&
        Objects.equals(this.password, userParameters.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserParameters {\n");
    
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
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
