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
 * CPU description
 */
@ApiModel(description = "CPU description")

public class CPU   {
  @JsonProperty("model")
  private String model = null;

  @JsonProperty("manufacturer")
  private String manufacturer = null;

  @JsonProperty("cores")
  private Integer cores = null;

  public CPU model(String model) {
    this.model = model;
    return this;
  }

  /**
   * CPU model name
   * @return model
   **/
  @JsonProperty("model")
  @ApiModelProperty(value = "CPU model name")
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public CPU manufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  /**
   * CPU manufacturer
   * @return manufacturer
   **/
  @JsonProperty("manufacturer")
  @ApiModelProperty(value = "CPU manufacturer")
  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public CPU cores(Integer cores) {
    this.cores = cores;
    return this;
  }

  /**
   * Number of cores
   * @return cores
   **/
  @JsonProperty("cores")
  @ApiModelProperty(value = "Number of cores")
  public Integer getCores() {
    return cores;
  }

  public void setCores(Integer cores) {
    this.cores = cores;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CPU CPU = (CPU) o;
    return Objects.equals(this.model, CPU.model) &&
        Objects.equals(this.manufacturer, CPU.manufacturer) &&
        Objects.equals(this.cores, CPU.cores);
  }

  @Override
  public int hashCode() {
    return Objects.hash(model, manufacturer, cores);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CPU {\n");
    
    sb.append("    model: ").append(toIndentedString(model)).append("\n");
    sb.append("    manufacturer: ").append(toIndentedString(manufacturer)).append("\n");
    sb.append("    cores: ").append(toIndentedString(cores)).append("\n");
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
