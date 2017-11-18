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
 * Location within a document page expressed in page number and pixel region coordinates on the page
 */
@ApiModel(description = "Location within a document page expressed in page number and pixel region coordinates on the page")

public class DocumentRegion   {
  @JsonProperty("page")
  private Integer page = null;

  @JsonProperty("left")
  private Integer left = null;

  @JsonProperty("top")
  private Integer top = null;

  @JsonProperty("right")
  private Integer right = null;

  @JsonProperty("bottom")
  private Integer bottom = null;

  public DocumentRegion page(Integer page) {
    this.page = page;
    return this;
  }

  /**
   * Page number
   * @return page
   **/
  @JsonProperty("page")
  @ApiModelProperty(value = "Page number")
  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public DocumentRegion left(Integer left) {
    this.left = left;
    return this;
  }

  /**
   * Left pixel coordinate
   * @return left
   **/
  @JsonProperty("left")
  @ApiModelProperty(value = "Left pixel coordinate")
  public Integer getLeft() {
    return left;
  }

  public void setLeft(Integer left) {
    this.left = left;
  }

  public DocumentRegion top(Integer top) {
    this.top = top;
    return this;
  }

  /**
   * Top pixel coordinate
   * @return top
   **/
  @JsonProperty("top")
  @ApiModelProperty(value = "Top pixel coordinate")
  public Integer getTop() {
    return top;
  }

  public void setTop(Integer top) {
    this.top = top;
  }

  public DocumentRegion right(Integer right) {
    this.right = right;
    return this;
  }

  /**
   * Right pixel coordinate
   * @return right
   **/
  @JsonProperty("right")
  @ApiModelProperty(value = "Right pixel coordinate")
  public Integer getRight() {
    return right;
  }

  public void setRight(Integer right) {
    this.right = right;
  }

  public DocumentRegion bottom(Integer bottom) {
    this.bottom = bottom;
    return this;
  }

  /**
   * Bottom pixel coordinate
   * @return bottom
   **/
  @JsonProperty("bottom")
  @ApiModelProperty(value = "Bottom pixel coordinate")
  public Integer getBottom() {
    return bottom;
  }

  public void setBottom(Integer bottom) {
    this.bottom = bottom;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DocumentRegion documentRegion = (DocumentRegion) o;
    return Objects.equals(this.page, documentRegion.page) &&
        Objects.equals(this.left, documentRegion.left) &&
        Objects.equals(this.top, documentRegion.top) &&
        Objects.equals(this.right, documentRegion.right) &&
        Objects.equals(this.bottom, documentRegion.bottom);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, left, top, right, bottom);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DocumentRegion {\n");
    
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    left: ").append(toIndentedString(left)).append("\n");
    sb.append("    top: ").append(toIndentedString(top)).append("\n");
    sb.append("    right: ").append(toIndentedString(right)).append("\n");
    sb.append("    bottom: ").append(toIndentedString(bottom)).append("\n");
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
