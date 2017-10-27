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
 * Operating system information
 */
@ApiModel(description = "Operating system information")

public class OperatingSystem   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("distribution")
  private String distribution = null;

  @JsonProperty("majorVersion")
  private String majorVersion = null;

  @JsonProperty("minorVersion")
  private String minorVersion = null;

  public OperatingSystem name(String name) {
    this.name = name;
    return this;
  }

  /**
   * OS major version e.g., Linux
   * @return name
   **/
  @JsonProperty("name")
  @ApiModelProperty(value = "OS major version e.g., Linux")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public OperatingSystem distribution(String distribution) {
    this.distribution = distribution;
    return this;
  }

  /**
   * Distribution, e.g, Debian or Ubuntu
   * @return distribution
   **/
  @JsonProperty("distribution")
  @ApiModelProperty(value = "Distribution, e.g, Debian or Ubuntu")
  public String getDistribution() {
    return distribution;
  }

  public void setDistribution(String distribution) {
    this.distribution = distribution;
  }

  public OperatingSystem majorVersion(String majorVersion) {
    this.majorVersion = majorVersion;
    return this;
  }

  /**
   * OS major version e.g., &#39;Waxing Wombat&#39; or 10
   * @return majorVersion
   **/
  @JsonProperty("majorVersion")
  @ApiModelProperty(value = "OS major version e.g., 'Waxing Wombat' or 10")
  public String getMajorVersion() {
    return majorVersion;
  }

  public void setMajorVersion(String majorVersion) {
    this.majorVersion = majorVersion;
  }

  public OperatingSystem minorVersion(String minorVersion) {
    this.minorVersion = minorVersion;
    return this;
  }

  /**
   * OS minor version e.g., &#39;0&#39;
   * @return minorVersion
   **/
  @JsonProperty("minorVersion")
  @ApiModelProperty(value = "OS minor version e.g., '0'")
  public String getMinorVersion() {
    return minorVersion;
  }

  public void setMinorVersion(String minorVersion) {
    this.minorVersion = minorVersion;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OperatingSystem operatingSystem = (OperatingSystem) o;
    return Objects.equals(this.name, operatingSystem.name) &&
        Objects.equals(this.distribution, operatingSystem.distribution) &&
        Objects.equals(this.majorVersion, operatingSystem.majorVersion) &&
        Objects.equals(this.minorVersion, operatingSystem.minorVersion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, distribution, majorVersion, minorVersion);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OperatingSystem {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    distribution: ").append(toIndentedString(distribution)).append("\n");
    sb.append("    majorVersion: ").append(toIndentedString(majorVersion)).append("\n");
    sb.append("    minorVersion: ").append(toIndentedString(minorVersion)).append("\n");
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

