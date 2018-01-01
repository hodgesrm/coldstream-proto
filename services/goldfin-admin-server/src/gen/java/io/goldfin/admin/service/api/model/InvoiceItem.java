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
import com.fasterxml.jackson.annotation.JsonValue;
import io.goldfin.admin.service.api.model.DocumentRegion;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import javax.validation.constraints.*;

/**
 * Common fields for all invoice line items
 */
@ApiModel(description = "Common fields for all invoice line items")

public class InvoiceItem   {
  @JsonProperty("itemId")
  private String itemId = null;

  @JsonProperty("resourceId")
  private String resourceId = null;

  @JsonProperty("unitAmount")
  private BigDecimal unitAmount = null;

  @JsonProperty("units")
  private Integer units = null;

  @JsonProperty("totalAmount")
  private BigDecimal totalAmount = null;

  @JsonProperty("currency")
  private String currency = null;

  @JsonProperty("startDate")
  private String startDate = null;

  @JsonProperty("endDate")
  private String endDate = null;

  @JsonProperty("region")
  private DocumentRegion region = null;

  @JsonProperty("inventoryId")
  private String inventoryId = null;

  /**
   * Gets or Sets inventoryType
   */
  public enum InventoryTypeEnum {
    HOST("HOST");

    private String value;

    InventoryTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static InventoryTypeEnum fromValue(String text) {
      for (InventoryTypeEnum b : InventoryTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("inventoryType")
  private InventoryTypeEnum inventoryType = null;

  public InvoiceItem itemId(String itemId) {
    this.itemId = itemId;
    return this;
  }

  /**
   * Invoice item ID if specified
   * @return itemId
   **/
  @JsonProperty("itemId")
  @ApiModelProperty(value = "Invoice item ID if specified")
  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public InvoiceItem resourceId(String resourceId) {
    this.resourceId = resourceId;
    return this;
  }

  /**
   * Inventory resource ID
   * @return resourceId
   **/
  @JsonProperty("resourceId")
  @ApiModelProperty(value = "Inventory resource ID")
  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public InvoiceItem unitAmount(BigDecimal unitAmount) {
    this.unitAmount = unitAmount;
    return this;
  }

  /**
   * Cost per unit
   * @return unitAmount
   **/
  @JsonProperty("unitAmount")
  @ApiModelProperty(value = "Cost per unit")
  public BigDecimal getUnitAmount() {
    return unitAmount;
  }

  public void setUnitAmount(BigDecimal unitAmount) {
    this.unitAmount = unitAmount;
  }

  public InvoiceItem units(Integer units) {
    this.units = units;
    return this;
  }

  /**
   * Number of units
   * @return units
   **/
  @JsonProperty("units")
  @ApiModelProperty(value = "Number of units")
  public Integer getUnits() {
    return units;
  }

  public void setUnits(Integer units) {
    this.units = units;
  }

  public InvoiceItem totalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
    return this;
  }

  /**
   * Total cost for all units
   * @return totalAmount
   **/
  @JsonProperty("totalAmount")
  @ApiModelProperty(value = "Total cost for all units")
  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public InvoiceItem currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Item currency
   * @return currency
   **/
  @JsonProperty("currency")
  @ApiModelProperty(value = "Item currency")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public InvoiceItem startDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Begining of the time range
   * @return startDate
   **/
  @JsonProperty("startDate")
  @ApiModelProperty(value = "Begining of the time range")
  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public InvoiceItem endDate(String endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * End of the time range
   * @return endDate
   **/
  @JsonProperty("endDate")
  @ApiModelProperty(value = "End of the time range")
  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public InvoiceItem region(DocumentRegion region) {
    this.region = region;
    return this;
  }

  /**
   * Get region
   * @return region
   **/
  @JsonProperty("region")
  @ApiModelProperty(value = "")
  public DocumentRegion getRegion() {
    return region;
  }

  public void setRegion(DocumentRegion region) {
    this.region = region;
  }

  public InvoiceItem inventoryId(String inventoryId) {
    this.inventoryId = inventoryId;
    return this;
  }

  /**
   * Id of an inventory description
   * @return inventoryId
   **/
  @JsonProperty("inventoryId")
  @ApiModelProperty(value = "Id of an inventory description")
  public String getInventoryId() {
    return inventoryId;
  }

  public void setInventoryId(String inventoryId) {
    this.inventoryId = inventoryId;
  }

  public InvoiceItem inventoryType(InventoryTypeEnum inventoryType) {
    this.inventoryType = inventoryType;
    return this;
  }

  /**
   * Get inventoryType
   * @return inventoryType
   **/
  @JsonProperty("inventoryType")
  @ApiModelProperty(value = "")
  public InventoryTypeEnum getInventoryType() {
    return inventoryType;
  }

  public void setInventoryType(InventoryTypeEnum inventoryType) {
    this.inventoryType = inventoryType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceItem invoiceItem = (InvoiceItem) o;
    return Objects.equals(this.itemId, invoiceItem.itemId) &&
        Objects.equals(this.resourceId, invoiceItem.resourceId) &&
        Objects.equals(this.unitAmount, invoiceItem.unitAmount) &&
        Objects.equals(this.units, invoiceItem.units) &&
        Objects.equals(this.totalAmount, invoiceItem.totalAmount) &&
        Objects.equals(this.currency, invoiceItem.currency) &&
        Objects.equals(this.startDate, invoiceItem.startDate) &&
        Objects.equals(this.endDate, invoiceItem.endDate) &&
        Objects.equals(this.region, invoiceItem.region) &&
        Objects.equals(this.inventoryId, invoiceItem.inventoryId) &&
        Objects.equals(this.inventoryType, invoiceItem.inventoryType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemId, resourceId, unitAmount, units, totalAmount, currency, startDate, endDate, region, inventoryId, inventoryType);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceItem {\n");
    
    sb.append("    itemId: ").append(toIndentedString(itemId)).append("\n");
    sb.append("    resourceId: ").append(toIndentedString(resourceId)).append("\n");
    sb.append("    unitAmount: ").append(toIndentedString(unitAmount)).append("\n");
    sb.append("    units: ").append(toIndentedString(units)).append("\n");
    sb.append("    totalAmount: ").append(toIndentedString(totalAmount)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    region: ").append(toIndentedString(region)).append("\n");
    sb.append("    inventoryId: ").append(toIndentedString(inventoryId)).append("\n");
    sb.append("    inventoryType: ").append(toIndentedString(inventoryType)).append("\n");
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

