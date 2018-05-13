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
import javax.validation.constraints.*;

/**
 * User settable invoice parameters
 */
@ApiModel(description = "User settable invoice parameters")

public class InvoiceParameters   {
  @JsonProperty("description")
  private String description = null;

  @JsonProperty("tags")
  private String tags = null;

  public InvoiceParameters description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A user-provided description of the invoice
   * @return description
   **/
  @JsonProperty("description")
  @ApiModelProperty(value = "A user-provided description of the invoice")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public InvoiceParameters tags(String tags) {
    this.tags = tags;
    return this;
  }

  /**
   * A user-provided list of name-value pairs that describe the invoice
   * @return tags
   **/
  @JsonProperty("tags")
  @ApiModelProperty(value = "A user-provided list of name-value pairs that describe the invoice")
  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceParameters invoiceParameters = (InvoiceParameters) o;
    return Objects.equals(this.description, invoiceParameters.description) &&
        Objects.equals(this.tags, invoiceParameters.tags);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, tags);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceParameters {\n");
    
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
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

