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
import com.fasterxml.jackson.annotation.JsonValue;
import io.goldfin.admin.service.api.model.TagSet;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * Vendor description
 */
@ApiModel(description = "Vendor description")

public class Vendor   {
  @JsonProperty("id")
  private UUID id = null;

  @JsonProperty("identifier")
  private String identifier = null;

  @JsonProperty("name")
  private String name = null;

  /**
   * Current state of the vendor.  Active vendors have current inventory and invoices.
   */
  public enum StateEnum {
    ACTIVE("ACTIVE"),
    
    INACTIVE("INACTIVE");

    private String value;

    StateEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StateEnum fromValue(String text) {
      for (StateEnum b : StateEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("state")
  private StateEnum state = null;

  @JsonProperty("tags")
  private TagSet tags = null;

  @JsonProperty("creationDate")
  private String creationDate = null;

  public Vendor id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Unique vendor id
   * @return id
   **/
  @JsonProperty("id")
  @ApiModelProperty(value = "Unique vendor id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Vendor identifier(String identifier) {
    this.identifier = identifier;
    return this;
  }

  /**
   * Standard key used to describe vendor for accounting purposes
   * @return identifier
   **/
  @JsonProperty("identifier")
  @ApiModelProperty(value = "Standard key used to describe vendor for accounting purposes")
  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public Vendor name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Vendor display name
   * @return name
   **/
  @JsonProperty("name")
  @ApiModelProperty(value = "Vendor display name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Vendor state(StateEnum state) {
    this.state = state;
    return this;
  }

  /**
   * Current state of the vendor.  Active vendors have current inventory and invoices.
   * @return state
   **/
  @JsonProperty("state")
  @ApiModelProperty(value = "Current state of the vendor.  Active vendors have current inventory and invoices.")
  public StateEnum getState() {
    return state;
  }

  public void setState(StateEnum state) {
    this.state = state;
  }

  public Vendor tags(TagSet tags) {
    this.tags = tags;
    return this;
  }

  /**
   * Get tags
   * @return tags
   **/
  @JsonProperty("tags")
  @ApiModelProperty(value = "")
  public TagSet getTags() {
    return tags;
  }

  public void setTags(TagSet tags) {
    this.tags = tags;
  }

  public Vendor creationDate(String creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  /**
   * Date record was created
   * @return creationDate
   **/
  @JsonProperty("creationDate")
  @ApiModelProperty(value = "Date record was created")
  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Vendor vendor = (Vendor) o;
    return Objects.equals(this.id, vendor.id) &&
        Objects.equals(this.identifier, vendor.identifier) &&
        Objects.equals(this.name, vendor.name) &&
        Objects.equals(this.state, vendor.state) &&
        Objects.equals(this.tags, vendor.tags) &&
        Objects.equals(this.creationDate, vendor.creationDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, identifier, name, state, tags, creationDate);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Vendor {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    identifier: ").append(toIndentedString(identifier)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    creationDate: ").append(toIndentedString(creationDate)).append("\n");
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

