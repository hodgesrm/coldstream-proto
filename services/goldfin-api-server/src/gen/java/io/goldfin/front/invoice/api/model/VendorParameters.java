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
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;

/**
 * Parameters used for vendor creation or update requests
 */
@ApiModel(description = "Parameters used for vendor creation or update requests")

public class VendorParameters   {
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

  public VendorParameters identifier(String identifier) {
    this.identifier = identifier;
    return this;
  }

  /**
   * Key used to describe vendor for accounting purposes
   * @return identifier
   **/
  @JsonProperty("identifier")
  @ApiModelProperty(value = "Key used to describe vendor for accounting purposes")
  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public VendorParameters name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Vendor name
   * @return name
   **/
  @JsonProperty("name")
  @ApiModelProperty(value = "Vendor name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public VendorParameters state(StateEnum state) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VendorParameters vendorParameters = (VendorParameters) o;
    return Objects.equals(this.identifier, vendorParameters.identifier) &&
        Objects.equals(this.name, vendorParameters.name) &&
        Objects.equals(this.state, vendorParameters.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(identifier, name, state);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VendorParameters {\n");
    
    sb.append("    identifier: ").append(toIndentedString(identifier)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
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

