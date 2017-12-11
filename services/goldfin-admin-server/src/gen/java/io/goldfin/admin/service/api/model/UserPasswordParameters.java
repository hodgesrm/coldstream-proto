/*
 * Goldfin Service Admin API
 * REST API for Goldfin Service Administration
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
import javax.validation.constraints.*;

/**
 * Parameters to change a user password
 */
@ApiModel(description = "Parameters to change a user password")

public class UserPasswordParameters   {
  @JsonProperty("oldPassword")
  private String oldPassword = null;

  @JsonProperty("newPassword")
  private String newPassword = null;

  public UserPasswordParameters oldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
    return this;
  }

  /**
   * Existing user password.  Required to change user password.
   * @return oldPassword
   **/
  @JsonProperty("oldPassword")
  @ApiModelProperty(value = "Existing user password.  Required to change user password.")
  public String getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public UserPasswordParameters newPassword(String newPassword) {
    this.newPassword = newPassword;
    return this;
  }

  /**
   * New user password.
   * @return newPassword
   **/
  @JsonProperty("newPassword")
  @ApiModelProperty(value = "New user password.")
  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserPasswordParameters userPasswordParameters = (UserPasswordParameters) o;
    return Objects.equals(this.oldPassword, userPasswordParameters.oldPassword) &&
        Objects.equals(this.newPassword, userPasswordParameters.newPassword);
  }

  @Override
  public int hashCode() {
    return Objects.hash(oldPassword, newPassword);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserPasswordParameters {\n");
    
    sb.append("    oldPassword: ").append(toIndentedString(oldPassword)).append("\n");
    sb.append("    newPassword: ").append(toIndentedString(newPassword)).append("\n");
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

